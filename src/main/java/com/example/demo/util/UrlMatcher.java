package com.example.demo.util;

import org.assertj.core.util.Sets;
import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/5/20.
 */
public class UrlMatcher {

    public static final Set<String> STATIC_URL_SUFFIX_LIST = (Set) Sets.newLinkedHashSet(new String[]{"js", "gif", "jpg", "jpeg", "ico", "css"}).stream().map((x) -> {
        return "." + x;
    }).collect(Collectors.toSet());
    private AntPathMatcher pathMatcher = new AntPathMatcher();
    private Set<String> antUrls = new LinkedHashSet();
    private Set<String> suffixUrls = new LinkedHashSet();

    public UrlMatcher(Set<String> antUrls) {
        this.antUrls = antUrls;
    }

    public UrlMatcher(Set<String> antUrls, Set<String> suffixUrls) {
        this.antUrls = antUrls;
        this.suffixUrls = suffixUrls;
    }

    // TODO
    public boolean ignore(String url) {
        System.out.println("=======ignore=======");
        return true;
    }

}
