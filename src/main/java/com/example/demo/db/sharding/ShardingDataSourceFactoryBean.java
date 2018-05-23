package com.example.demo.db.sharding;

import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.config.ShardingPropertiesConstant;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.ShardingDataSource;
import com.example.demo.constant.ApplicationConstant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

import java.util.Properties;

/**
 * Created by liunanhua on 2018/5/9.
 */
public abstract class ShardingDataSourceFactoryBean implements FactoryBean, BeanFactoryAware {

    protected BeanFactory beanFactory;

    public ShardingDataSourceFactoryBean() {
    }

    @Override
    public ShardingDataSource getObject() throws Exception {
        return new ShardingDataSource(this.createShardingRule(), this.createProperties());
    }

    protected abstract ShardingRule createShardingRule();

    protected Properties createProperties() {
        ApplicationConstant applicationConstant = (ApplicationConstant)this.beanFactory.getBean(ApplicationConstant.class);
        Properties properties = new Properties();
        properties.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(), String.valueOf(applicationConstant.sjdbcShowLog));
        if(applicationConstant.sjdbcExecutorSize > 0) {
            properties.setProperty(ShardingPropertiesConstant.EXECUTOR_SIZE.getKey(), String.valueOf(applicationConstant.sjdbcExecutorSize));
        }
        return properties;
    }

    @Override
    public Class<?> getObjectType() {
        return ShardingDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }


}
