package com.example.demo.config;

import com.example.demo.constant.ApplicationConstant;
import com.example.demo.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.example.demo.constant.Constants.RAW_REST_TEMPLATE;


@ComponentScan({"com.example.demo"})
@MapperScan(basePackages = "com.example.demo.dao")//mapper接口的路径
public class BaseConfiguration extends WebMvcConfigurerAdapter {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars*")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }

    @Bean
    public ApplicationContextHolder applicationContextHolder(ApplicationConstant applicationConstant) {
        ApplicationContextHolder applicationContextHolder = ApplicationContextHolder.getInstance();
        applicationContextHolder.init(applicationConstant);
        return applicationContextHolder;
    }

    @Bean
    public ApplicationConstant applicationConstant() {
        return new ApplicationConstant();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JacksonUtils.mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Primary
    @Bean
    public okhttp3.OkHttpClient okHttpClient(ApplicationConstant applicationConstant){
        return OkHttpUtils.okHttpClientBuilder(applicationConstant).build();
    }

    @LoadBalanced
    @Bean
    @Primary
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                     OkHttpClient okHttpClient) {
        return EnhancedRestTemplate.assembleRestTemplate(mappingJackson2HttpMessageConverter, okHttpClient);
    }

    @Bean(name = RAW_REST_TEMPLATE)
    public RestTemplate rawRestTemplate(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter,
                                        OkHttpClient okHttpClient) {

        return EnhancedRestTemplate.assembleRestTemplate(mappingJackson2HttpMessageConverter, okHttpClient);
    }

    @Bean
    public RefreshScope scope(){
        return new RefreshScope();
    }

    @Bean
    public ContextRefresher contextRefresher(ConfigurableApplicationContext context,
                                             RefreshScope scope) {
        return new MyContextRefresher(context, scope);
    }

    @Bean(name = "hostName")
    public String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "mybatis-sharding";
    }





}
