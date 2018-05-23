package com.example.demo.db;

public interface DataSourceNames {

    /**
     * 默认数据源前缀
     */
    String DB_DEFAULT_PREFIX_KEY = "spring.datasource";
    /**
     * 非默认数据源前缀
     */
    String DB_CUSTOM_PREFIX_KEY = "custom.datasource";


    /**
     * 读库
     */
    String DB_READ = "custom.datasource.read";
    /**
     * 分库写
     */
    String DB_WRITE = "custom.datasource.write";


    /** ===================== sharding start ============================ */

    /**
     * ShardingTableRule
     */
    String DB_SHARDING_TABLE_BASE_NAME = "t_user";
    /**
     * 以什么字段分表
     */
    String DB_SHARDING_TABLE_COLUMN = "id";

    String SHARDING_RP_WRITE = DB_WRITE;

    String SHARDING_RP_READ = DB_READ;

    /** ===================== sharding end ============================ */

}
