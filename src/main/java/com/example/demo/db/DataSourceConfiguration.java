package com.example.demo.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.ShardingDataSource;
import com.example.demo.db.mybatis.MybatisSQLPerformanceInterceptor;
import com.example.demo.db.mybatis.MybatisTimeoutInterceptor;
import com.example.demo.db.sharding.RecordShardingDataSourceFactoryBean;
import com.example.demo.db.sharding.ShardingDataSourceCollector;
import com.example.demo.exception.AppBusinessException;
import com.google.common.collect.Lists;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


@Configuration
@EnableTransactionManagement
public class DataSourceConfiguration implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * list中的第一个为默认数据源
     */
    @Bean
    public MultipleDataSource multipleDataSource(){
        return new MultipleDataSource(Lists.newArrayList(DataSourceNames.DB_READ, DataSourceNames.DB_WRITE));
    }

    @Bean
    public TargetDataSourceInterceptor dataSourceInterceptor(){
        return new TargetDataSourceInterceptor();
    }


    @Bean
    @Qualifier(DataSourceNames.SHARDING_RP_READ)
    public RecordShardingDataSourceFactoryBean readShardingDataSource() {
        return new RecordShardingDataSourceFactoryBean(Boolean.FALSE);
    }
    @Bean
    @Qualifier(DataSourceNames.SHARDING_RP_WRITE)
    public RecordShardingDataSourceFactoryBean recordShardingDataSource() {
        return new RecordShardingDataSourceFactoryBean(Boolean.TRUE);
    }
    @Bean
    public ShardingDataSourceCollector shardingDataSourceCollector(
            @Qualifier(DataSourceNames.SHARDING_RP_READ) ShardingDataSource readShardingDataSource,
            @Qualifier(DataSourceNames.SHARDING_RP_WRITE) ShardingDataSource recordShardingDataSource) {
        return () -> {
            Map<String, ShardingDataSource> shardingDataSourceMap = new HashMap<>();
            shardingDataSourceMap.put(DataSourceNames.SHARDING_RP_READ, readShardingDataSource);
            shardingDataSourceMap.put(DataSourceNames.SHARDING_RP_WRITE, recordShardingDataSource);
            return shardingDataSourceMap;
        };
    }



    @Bean(name = "dynamicDataSource")
    public AbstractRoutingDataSource dataSource(MultipleDataSource multipleDataSource, MultipleDataSourceInitializer initializer,ShardingDataSourceCollector shardingDataSourceCollector) {
        //dynamicDataSource保存所有数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        //如果指定的dataSource key不存在, 则报错
        dynamicDataSource.setLenientFallback(false);

        Map<Object, Object> dataSourceMap = new HashMap<>();
        for (String ds : multipleDataSource.getDataSources()) {
            DruidDataSource dataSource = applicationContext.getBean(ds, DruidDataSource.class);
            //设置默认数据库
            if (ds.equalsIgnoreCase(multipleDataSource.getDefaultDataSource())) {
                dynamicDataSource.setDefaultTargetDataSource(dataSource);
            }
            dataSourceMap.put(ds, dataSource);
        }

        //向dataSourceMap加入shardingDataSource
        Map<String, ShardingDataSource> shardingDataSourceMap = shardingDataSourceCollector.collectShardingDataSources();
        if (shardingDataSourceMap != null && !shardingDataSourceMap.isEmpty()) {
            dataSourceMap.putAll(shardingDataSourceMap);
        }

        logger.info("DataSourceMap.keySet(): {}", dataSourceMap.keySet());
        /**
         * sharding datasource bean 依赖顺序
         *
         * dynamicDataSource -> ShardingDataSourceCollector -> ShardingDataSource(ShardingDataSourceFactoryBean) ->
         * multipleDataSourceInitializer -> MultipleDataSource, DataSourcePropertiesProcessor
         */
        dynamicDataSource.setTargetDataSources(dataSourceMap);

        return dynamicDataSource;
    }


    @Bean(name = "transactionManager")
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager(AbstractRoutingDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(AbstractRoutingDataSource dynamicDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dynamicDataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper*/**/*.xml"));
        sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource("classpath:mybatis-config.xml"));

        Interceptor[] its = new Interceptor[2];
        its[0] = new MybatisTimeoutInterceptor();
        its[1] = new MybatisSQLPerformanceInterceptor(applicationContext);
        sessionFactory.setPlugins(its);

        Properties properties = new Properties();
        properties.put("dialect", "mysql");
        sessionFactory.setConfigurationProperties(properties);
        return sessionFactory.getObject();
    }


    /**
     * 根据配置的MultipleDataSource, 创建多个数据源bean
     *
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        MultipleDataSource multipleDataSource = applicationContext.getBean(MultipleDataSource.class);
        if (multipleDataSource == null) {
            throw new AppBusinessException("multipleDataSource cannot be null, " +
                    "配置了MultipleDataSourceConfiguration就一定要定义MultipleDataSource");
        }

        Set<String> dataSources = multipleDataSource.getDataSources();
        String defaultDataSource = multipleDataSource.getDefaultDataSource();
        for (String ds : dataSources) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
            if (ds.equalsIgnoreCase(defaultDataSource)) {
                builder.getBeanDefinition().setPrimary(true);
            }
            registry.registerBeanDefinition(ds, builder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Bean(name = MultipleDataSourceInitializer.BEAN_NAME)
    public MultipleDataSourceInitializer multipleDataSourceInitializer(MultipleDataSource multipleDataSource,
                                                                       DataSourcePropertiesProcessor processor) throws Exception {
        for (String ds : multipleDataSource.getDataSources()) {
            DruidDataSource dataSource = applicationContext.getBean(ds, DruidDataSource.class);
            //读取配置文件
            processor.postProcessBeforeInitialization(dataSource, ds, ds);
            initDruidDataSource(dataSource);
            //如果没有配置socket超时, 设置socket超时时间为3分钟
//            Properties connectProperties = dataSource.getConnectProperties();
//            if(connectProperties != null && !connectProperties.contains("socketTimeout")) {
//                dataSource.addConnectionProperty("socketTimeout", "180000");
//            }
        }
        return MultipleDataSourceInitializer.DEFAULT;
    }

    /**
     * 初始化druid数据源配置, 现在不需要在配置文件里配置一些默认参数了
     *
     * @param dataSource
     */
    private void initDruidDataSource(DruidDataSource dataSource) {
        try {
            //设置默认参数
            if (dataSource.getMaxActive() == 8 || dataSource.getMaxActive() == 5) {
                dataSource.setMaxActive(100);
            }

            if (dataSource.getInitialSize() == 0 || dataSource.getInitialSize() == 1) {
                dataSource.setInitialSize(10);
            }

            if (dataSource.getMinIdle() == 0) {
                dataSource.setMinIdle(10);
            }

            if (!dataSource.isPoolPreparedStatements()) {
                dataSource.setMaxPoolPreparedStatementPerConnectionSize(5);
            }

            //设置获取连接的最大等待时间为10s
            if (dataSource.getMaxWait() < 0 || dataSource.getMaxWait() > 5000L) {
                dataSource.setMaxWait(5000L);
            }

            if (dataSource.getValidationQuery() == null) {
                dataSource.setValidationQuery("SELECT 'x'");
            }
        } catch (Exception e) {
            logger.error("初始化druid数据源发生错误, ex: " + e.getMessage());
        }
    }

}
