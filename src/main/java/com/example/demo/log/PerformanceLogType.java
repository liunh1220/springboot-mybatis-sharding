package com.example.demo.log;

/**
 * Created by Administrator on 2018/5/20.
 */
public enum PerformanceLogType {


    /**
     * Spring MVC 请求
     */
    SPRING_REQ,

    /**
     * Spring MVC 响应
     */
    SPRING_RESP,

    /**
     * REST TEMPLATE 请求
     */
    REST_REQ,

    /**
     * REST TEMPLATE 响应
     */
    REST_RESP,

    /**
     * FEIGN 请求
     */
    FEIGN_REQ,

    /**
     * FEIGN 响应
     */
    FEIGN_RESP,

    /**
     * OKHTTP 请求
     */
    OKHTTP_REQ,

    /**
     * OKHTTP 响应
     */
    OKHTTP_RESP,

    /**
     * SQL 请求
     */
    SQL_REQ,

    /**
     * SQL 响应
     */
    SQL_RESP,

    /**
     * MQ消息发送
     */
    MQ_SEND_REQ,

    /**
     * MQ消息接收开始
     */
    MQ_RECEIVE_REQ,

    /**
     * MQ消息接收与处理结束
     */
    MQ_RECEIVE_RESP;


}
