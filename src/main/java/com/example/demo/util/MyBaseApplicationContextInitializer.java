package com.example.demo.util;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by Administrator on 2018/5/21.
 */
public class MyBaseApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    //刷新配置的时候, spring cloud会构造context加载配置, 需要给这种临时的context加个配置属性
    //来判断当前context到底是不是临时的context
    public static final String TEMP_CONTEXT_NAME = "TD_CONFIG_CONTEXT";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if(applicationContext.getParent() != null) {
            //刷新配置的时候, spring cloud会构造context加载配置, 需要把这种临时的context过滤掉
            String configContext = applicationContext.getEnvironment().getProperty(TEMP_CONTEXT_NAME);
            if(configContext == null || !configContext.equalsIgnoreCase("true")) {
                ApplicationContextHolder.context = applicationContext;
            }
        }
    }

}
