package com.example.demo.db.mybatis;

import com.example.demo.db.DataSourceTimeoutResolver;
import com.example.demo.db.DataSourceHolder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.executor.statement.StatementUtil;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

/**
 * 设置data source超时时间
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class MybatisTimeoutInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(MybatisTimeoutInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Statement statement = (Statement) invocation.proceed();
        try {
            Integer transactionTimeout = (Integer) invocation.getArgs()[1];
            if (statement != null && statement.getQueryTimeout() <= 0) {
                // 如果mybatis设置了全局timeout, 或者sql单独设置了timeout, 或者设置了事务timeout
                // 就不会设置数据源timeout
                String dataSource = DataSourceHolder.getDataSourceName();
                DataSourceTimeoutResolver dataSourceTimeoutResolver = new DataSourceTimeoutResolver();
                Integer dataSourceTimeout = dataSourceTimeoutResolver.getDataSourceTimeoutInSeconds(dataSource);
                if (dataSourceTimeout != null) {
                    statement.setQueryTimeout(dataSourceTimeout);
                    StatementUtil.applyTransactionTimeout(statement, dataSourceTimeout, transactionTimeout);
                }
            }
        } catch (Exception e) {
            logger.error("MybatisTimeoutInterceptor error: " + e.getMessage(), e);
        }
        return statement;
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