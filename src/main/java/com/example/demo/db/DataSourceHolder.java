package com.example.demo.db;

/**
 * Created by liulanhua on 2018/5/23.
 */
public class DataSourceHolder {

    public static final ThreadLocal<String> DS = new ThreadLocal<>();

    public static String getDataSourceName() {
        return DS.get();
    }

    public static void putDataSourceName(String dataSource) {
        DS.set(dataSource);
    }

    public static void clear(){
        DS.remove();
    }



}
