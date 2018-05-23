package com.example.demo.db.mybatis;

import com.alibaba.druid.pool.DruidPooledStatement;
import com.example.demo.constant.ApplicationConstant;
import com.example.demo.db.DataSourceHolder;
import com.example.demo.log.*;
import com.example.demo.util.DateUtil;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * 获取执行的sql信息并打印
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
        , @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class MybatisSQLPerformanceInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisSQLPerformanceInterceptor.class);

    private ApplicationContext applicationContext;

    public MybatisSQLPerformanceInterceptor(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        SQLRespLog sqlRespLog = null;
        String error = null;
        Long start = System.currentTimeMillis();
        boolean pluginHasError = false;

        try {
            ApplicationConstant applicationConstant = applicationContext.getBean(ApplicationConstant.class);
            PerformanceLogLevel logLevel = applicationConstant.determinePerformanceLogType();
            if (logLevel != null && logLevel.equals(PerformanceLogLevel.NONE) || !applicationConstant.dsShowLog || applicationConstant.performanceLogIgnoreSql) {
                return invocation.proceed();
            }

            sqlRespLog = new SQLRespLog(PerformanceLogType.SQL_RESP, logLevel);
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = statementHandler.getBoundSql();
            String sql = PerformanceLog.trim(boundSql.getSql());
            LocalDateTime beginDate = LocalDateTime.now();
            String beginDateStr = DateUtil.format(beginDate, "yyyy-MM-dd HH:mm:ss.SSS");
            sqlRespLog.setAddDate(beginDateStr);
            sqlRespLog.setBeginDate(beginDateStr);
            sqlRespLog.setSql(sql);
            //数据源的key, 例如db.ds.read.common
            sqlRespLog.setDataSourceKey(DataSourceHolder.getDataSourceName());

            Object ob = Proxy.getInvocationHandler(statementHandler);
            Plugin plugin = (Plugin) ob;
            String sqlId = getSqlId(plugin);
            sqlRespLog.setSqlId(sqlId);

            Object[] objs = invocation.getArgs();
            Object obj = objs[0];
            String databaseType = null;
            if (obj instanceof Connection) {//mysql
                Connection c = (Connection) obj;
                databaseType = c.getCatalog();
            } else if (obj instanceof DruidPooledStatement) {//sqlserver
                DruidPooledStatement ds = (DruidPooledStatement) obj;
                Connection c = ds.getConnection();
                databaseType = c.getCatalog();
            }
            //连接的数据库名字, 例如www_Junte_com
            sqlRespLog.setDatabaseType(databaseType);

            //暂时不打印SQL_REQ
//            SQLReqLog sqlReqLog = new SQLReqLog(PerformanceLogType.SQL_REQ, logLevel);
//            sqlReqLog.setAddDate(beginDateStr);
//            sqlReqLog.setBeginDate(beginDateStr);
//            sqlReqLog.setSql(sql);
//            sqlReqLog.setDataSourceKey(sqlRespLog.getDataSourceKey());
//            sqlReqLog.setSqlId(sqlId);
//            sqlReqLog.setDatabaseType(databaseType);
//            logger.info(sqlReqLog.toString());

        } catch (Exception e) {
            pluginHasError = true;
            logger.error("MybatisSQLPerformanceInterceptor error: " + e.getMessage(), e);
        }

        Object statement = null;
        try {
            statement = invocation.proceed();
        } catch (InvocationTargetException e) {
            Throwable te = e.getTargetException();
            error = PerformanceLogUtil.logError(te);
            throw e;
        } catch (Exception e) {
            error = PerformanceLogUtil.logError(e);
            throw e;
        } finally {
            try {
                if (!pluginHasError) {

                    /**
                     * 以下代码是在sharding jdbc环境下，获取数据库名的。
                     * 考虑到可能没有覆盖到sharding jdbc所有的分库情况，暂时注释掉
                     *
                     Object[] objs = invocation.getArgs();
                     Object obj = objs[0];
                     logger.info("\nobj:"+obj.getClass().getName());
                     if (sqlRespLog.getDatabaseType() == null && Proxy.isProxyClass(obj.getClass())) {
                     obj = Proxy.getInvocationHandler(obj);
                     if (obj instanceof PreparedStatementLogger) {
                     PreparedStatementLogger pl = (PreparedStatementLogger) obj;
                     PreparedStatement preparedStatement = pl.getPreparedStatement();
                     if (preparedStatement instanceof DruidPooledPreparedStatement) {
                     DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) preparedStatement;
                     PreparedStatement pst = ps.getRawStatement();
                     String databaseType = pst.getConnection().getCatalog();
                     sqlRespLog.setDatabaseType(databaseType);
                     } else if (preparedStatement instanceof ShardingPreparedStatement) {
                     ShardingPreparedStatement shardingPreparedStatement = (ShardingPreparedStatement) pl.getPreparedStatement();
                     Collection<PreparedStatement> psCollection = shardingPreparedStatement.getRoutedStatements();
                     for (PreparedStatement dd : psCollection) {
                     DruidPooledPreparedStatement ps = (DruidPooledPreparedStatement) dd;
                     PreparedStatement pst = ps.getRawStatement();
                     String databaseType = pst.getConnection().getCatalog();
                     sqlRespLog.setDatabaseType(databaseType);
                     break;
                     }
                     }
                     }
                     }
                     */

                    long end = System.currentTimeMillis();
                    sqlRespLog.setError(error);
                    LocalDateTime endDate = LocalDateTime.now();
                    String endDateStr = DateUtil.format(endDate, "yyyy-MM-dd HH:mm:ss.SSS");
                    sqlRespLog.setEndDate(endDateStr);
                    sqlRespLog.setExecuteDate(endDateStr);
                    sqlRespLog.setSqlUsedTime(end - start);
                    logger.info(sqlRespLog.toString());
                }
            } catch (Exception e) {
                logger.error("MybatisSQLPerformanceInterceptor error: " + e.getMessage(), e);
            }
        }
        return statement;
    }

    /**
     * 获取SqlId
     *
     * @param plugin
     * @return
     */
    private String getSqlId(Plugin plugin) {
        try {
            Field[] fs = Plugin.class.getDeclaredFields();
            for (Field field : fs) {
                field.setAccessible(true);
                Object oo = field.get(plugin);
                if (oo instanceof RoutingStatementHandler) {
                    RoutingStatementHandler rh = (RoutingStatementHandler) oo;
                    Field[] fs2 = RoutingStatementHandler.class.getDeclaredFields();
                    for (Field field2 : fs2) {
                        field2.setAccessible(true);

                        Object rh2 = field2.get(rh);
                        if (rh2 instanceof BaseStatementHandler) {
                            BaseStatementHandler sh = (BaseStatementHandler) rh2;

                            Field[] fs3 = BaseStatementHandler.class.getDeclaredFields();
                            for (Field field3 : fs3) {
                                field3.setAccessible(true);

                                Object rh3 = field3.get(sh);
                                if (rh3 instanceof MappedStatement) {
                                    MappedStatement ms = (MappedStatement) rh3;
                                    String id = ms.getId();
                                    return id;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("MybatisSQLPerformanceInterceptor 获取 sqlId发生错误", e);
        }

        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties0) {
        // this.properties = properties0;
    }


}
