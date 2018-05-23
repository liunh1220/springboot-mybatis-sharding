package com.example.demo.db.sharding.read;

import com.example.demo.db.DataSourceNames;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadShardingInfo {

    private static final ReadShardingInfo INSTANCE = new ReadShardingInfo();

    private ReadShardingInfo() {
        init();
    }

    public static ReadShardingInfo getInstance() {
        return INSTANCE;
    }

    private final Map<String, String> columnToDataSourceMap = new LinkedHashMap<>();

    private final Map<String, String> readColumnToDataSourceMap = new LinkedHashMap<>();

    private final Map<String, String> dataSourceToColumnMap = new LinkedHashMap<>();

    private final Map<String, String> readDataSourceToColumnMap = new LinkedHashMap<>();


    public final List<String> shardingKeys = new ArrayList<>();

    private void init() {
        //按照这个顺序如果where里这几个字段都有, 先路由到account库
        //写库
        columnToDataSourceMap.put(DataSourceNames.DB_SHARDING_TABLE_COLUMN.toLowerCase(), DataSourceNames.DB_READ);

        shardingKeys.addAll(columnToDataSourceMap.keySet());

        dataSourceToColumnMap.putAll(columnToDataSourceMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));

       /* dataSourceToColumnMap.putAll(readColumnToDataSourceMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));*/

    }

    public String getDataSourceByColumn(String columnName) {
        return columnToDataSourceMap.get(columnName.toLowerCase());
    }

    public String getColumnToDataSourceMapInfo() {
        return columnToDataSourceMap.toString();
    }

    public String getColumnByDataSource(String dataSource) {
        return dataSourceToColumnMap.get(dataSource);
    }

    public String getDataSourceToColumnMapInfo() {
        return dataSourceToColumnMap.toString();
    }

}
