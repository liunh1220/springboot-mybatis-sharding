package com.example.demo.db.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.MultipleKeysTableShardingAlgorithm;
import com.example.demo.db.DataSourceHolder;
import com.example.demo.db.sharding.read.ReadShardingInfo;
import com.example.demo.db.sharding.write.ShardingInfo;
import com.example.demo.exception.AppBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public final class TableShardingAlgorithm implements MultipleKeysTableShardingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(TableShardingAlgorithm.class);

    private Boolean isWrite;
    private ShardingInfo shardingInfo;
    private ReadShardingInfo readShardingInfo;


    public TableShardingAlgorithm(boolean isWrite, ShardingInfo shardingInfo, ReadShardingInfo readShardingInfo) {
        this.isWrite = isWrite;
        this.shardingInfo = shardingInfo;
        this.readShardingInfo = readShardingInfo;
    }


    @Override
    public Collection<String> doSharding(final Collection<String> availableTargetNames, final Collection<ShardingValue<?>> shardingValues) {

        List<String> results = new ArrayList<>();
        String dataSourceName = DataSourceHolder.getDataSourceName();
        String shardingKey = null;
        if (dataSourceName != null) {
            if (this.isWrite) {
                shardingKey = shardingInfo.getColumnByDataSource(dataSourceName);
            } else {
                shardingKey = readShardingInfo.getColumnByDataSource(dataSourceName);
            }
        }

        if (shardingKey == null) {
            String dataSourceToColumnMap = null;
            if (this.isWrite) {
                dataSourceToColumnMap = shardingInfo.getDataSourceToColumnMapInfo();
            } else {
                dataSourceToColumnMap = readShardingInfo.getDataSourceToColumnMapInfo();
            }
            throw new AppBusinessException(String.format("dataSourceName为null或者根据" +
                    "dataSourceName没有找到对应的shardingKey, dataSourceName: %s, " +
                    "dataSourceToColumnMap: %s", dataSourceName, dataSourceToColumnMap));
        }

        for (ShardingValue shardingValue : shardingValues) {
            if (shardingKey.equalsIgnoreCase(shardingValue.getColumnName())) {

                Set<String> idValues = new HashSet<>();
                if (shardingValue.getValue() != null) {
                    idValues.add(String.valueOf(shardingValue.getValue()));
                }

                //如果是in语句包含多个查询条件的语句, 要从getValues里取值
                if (shardingValue.getValues() != null && !shardingValue.getValues().isEmpty()) {
                    idValues.addAll(shardingValue.getValues());
                }

                Set<String> targetTableNames = idValues.stream()
                        .map(idValue -> ShardingTableRule.generateTableName(shardingValue.getLogicTableName(), idValue).toLowerCase())
                        .collect(Collectors.toSet());

                Set<String> unknownTableNames = targetTableNames.stream()
                        .filter(targetTableName -> !availableTargetNames.contains(targetTableName))
                        .collect(Collectors.toSet());

                if (!unknownTableNames.isEmpty()) {
                    throw new AppBusinessException(String.format("计算出来的目标表名在传过来的物理表参数中不存在, " +
                                    "dataSourceName: %s, shardingKey: %s, unknownTableNames: %s, availableTargetNames: %s",
                            dataSourceName, shardingKey, unknownTableNames, availableTargetNames));
                }

                results.addAll(targetTableNames);

                break;
            }
        }

        if (results.isEmpty()) {
            throw new AppBusinessException(String.format("根据分表信息没有找到对应的物理表, dataSourceName: %s, shardingKey: %s, availableTargetNames: %s, shardingValues: %s",
                    dataSourceName, shardingKey, availableTargetNames, shardingValues));
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("sharding jdbc路由表, 路由结果: %s, 参数: dataSourceName: %s, shardingKey: %s, availableTargetNames: %s, shardingValues: %s",
                    results, dataSourceName, shardingKey, availableTargetNames, shardingValues));
        }

        return results;
    }

}
