package com.example.demo.log;

import com.example.demo.constant.ApplicationConstant;
import com.example.demo.util.UrlMatcher;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/20.
 */
public class PerformanceLogUtil {
    private static final List<String> IGNORE_URL_LIST = Lists.newArrayList(
            "/do_not_delete/*", "/**/encrypt", "/**/decrypt", "/fonts/**", "/css/**"
    );

    private static UrlMatcher urlMatcher = new UrlMatcher(new LinkedHashSet<>(IGNORE_URL_LIST), UrlMatcher.STATIC_URL_SUFFIX_LIST);


    public synchronized static void init(ApplicationConstant constant) {
        if (constant.performanceLogIgnoreUrls != null && constant.performanceLogIgnoreUrls.length > 0) {
            Set<String> antUrls = new LinkedHashSet<>(IGNORE_URL_LIST);
            for (String url : constant.performanceLogIgnoreUrls) {
                if (StringUtils.isNotBlank(url)) {
                    antUrls.add(url.trim());
                }
            }
            urlMatcher = new UrlMatcher(antUrls, UrlMatcher.STATIC_URL_SUFFIX_LIST);
        }
    }

    public static boolean canLog(String url, PerformanceLogLevel level, Logger logger) {

        if (level.equals(PerformanceLogLevel.NONE) || !logger.isInfoEnabled()) {
            return false;
        }


        if (url != null && urlMatcher.ignore(url)) return false;

        return true;
    }

    public static void main(String[] args) {

        IGNORE_URL_LIST.add("/login");
        IGNORE_URL_LIST.add("/register");
        IGNORE_URL_LIST.add("/path1/*");

        urlMatcher = new UrlMatcher(new LinkedHashSet<>(IGNORE_URL_LIST), UrlMatcher.STATIC_URL_SUFFIX_LIST);

        List<String> urls = Lists.newArrayList("/do_not_delete/check.html", "/do_not_delete", "/login",
                "/register", "/path1/123123", "/path2", "/123/encrypt", "/decrypt", "/123enrypt");

        urls.stream().filter(urlMatcher::ignore).forEach(x -> System.out.println(x));
        if (urls.stream().filter(urlMatcher::ignore).count() != 6) {
            throw new AssertionError();
        }
    }

    public static String logError(Throwable ex) {
        return ex == null ? null : (ex.getClass().getSimpleName() + ": " + ex.getMessage());
    }
}
