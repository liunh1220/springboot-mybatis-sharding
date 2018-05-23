package com.example.demo.db;

import com.example.demo.exception.AppBusinessException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/20.
 */
public class MultipleDataSource implements InitializingBean,ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MultipleDataSource.class);

    private volatile ApplicationContext applicationContext;

    private Set<String> dataSources;

    private String defaultDataSource;


    /**
     * list中的第一个为默认数据源
     *
     * @param dataSources
     */
    public MultipleDataSource(List<String> dataSources) {
        if (dataSources == null || dataSources.isEmpty()) {
            throw new AppBusinessException("MultipleDataSource dataSources cannot be empty!");
        }
        this.dataSources = new HashSet<>(dataSources);
        this.defaultDataSource = dataSources.get(0);
    }

    public Set<String> getDataSources() {
        return dataSources;
    }

    public String getDefaultDataSource() {
        return defaultDataSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            if (!isValidDataSource(defaultDataSource)) {
                throw new AppBusinessException("默认数据源配置读取失败, 没有在配置文件中进行配置? ds: " + defaultDataSource);
            }

            Set<String> filterDataSources = new HashSet<>();
            for (String ds : dataSources) {
                if (isValidDataSource(ds)) {
                    filterDataSources.add(ds);
                } else {
                    logger.warn("{}数据源初始化失败, 没有配置在配置文件中?", ds);
                }
            }

            this.dataSources = filterDataSources;

        } catch (Exception e) {
            logger.error("MultipleDataSource在过滤无效数据源的时候发生错误", e);
        }
    }

    /**
     * 检测数据源是否有效
     *
     * @param ds
     * @return
     */
    private boolean isValidDataSource(String ds) {

        Environment environment = applicationContext.getBean(Environment.class);
        String url = environment.getProperty(ds + ".url");
        String username = environment.getProperty(ds + ".username");
        String password = environment.getProperty(ds + ".password");

        return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
    }



}
