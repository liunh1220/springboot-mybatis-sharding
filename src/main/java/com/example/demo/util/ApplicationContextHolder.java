package com.example.demo.util;

import com.example.demo.constant.ApplicationConstant;
import com.example.demo.log.PerformanceLogUtil;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2018/5/21.
 */
public class ApplicationContextHolder {

    public volatile static ApplicationContext context;

    @Deprecated
    public volatile static ApplicationConstant constant;

    private static AtomicBoolean startup = new AtomicBoolean(false);

    public static final ApplicationContextHolder INSTANCE = new ApplicationContextHolder();

    private ApplicationContextHolder(){}

    public static ApplicationContextHolder getInstance() {
        return INSTANCE;
    }

    public synchronized void init(ApplicationConstant applicationConstant) {
        constant = applicationConstant;
        PerformanceLogUtil.init(constant);
    }

    public static boolean tryStartup() {
        return startup.compareAndSet(false, true);
    }

    public static boolean isStartup() {
        return startup.get();
    }


}
