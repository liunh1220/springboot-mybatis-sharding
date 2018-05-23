package com.example.demo.db.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.MultipleKeysDatabaseShardingAlgorithm;
import com.example.demo.db.sharding.write.ShardingInfo;
import com.example.demo.exception.AppBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class DatabaseShardingAlgorithm implements MultipleKeysDatabaseShardingAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseShardingAlgorithm.class);

    private ShardingInfo shardingInfo = ShardingInfo.getInstance();

    @Override
    public Collection<String> doSharding(final Collection<String> availableTargetNames, final Collection<ShardingValue<?>> shardingValues) {

        List<String> shardingKeys = getShardingKey(shardingValues, shardingInfo.shardingKeys);
        List<String> dataSources = new ArrayList<>();
        for (String shardingKey : shardingKeys) {
            if (shardingKey == null) {
                String columnNames = shardingValues.stream().map(ShardingValue::getColumnName).collect(Collectors.toList()).toString();
                throw new AppBusinessException(String.format("查询的列名中没有包含shardingKey, columnNames: %s, shardingKeys: %s",
                        shardingInfo.shardingKeys.toString(), columnNames));
            }
            String dataSource = shardingInfo.getDataSourceByColumn(shardingKey);
            if (dataSource == null) {
                throw new AppBusinessException(String.format("根据shardingKey没有找到对应的数据源, shardingKey: %s, columnToDataSourceMap: %s",
                        shardingKey, shardingInfo.getColumnToDataSourceMapInfo()));
            }

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("sharding jdbc路由数据库, 路由结果: %s, 参数: shardingKey: %s, availableTargetNames: %s, shardingValues: %s",
                        dataSource, shardingKey, availableTargetNames, shardingValues));
            }
            dataSources.add(dataSource);

        }

        return dataSources;
    }

    private List<String> getShardingKey(Collection<ShardingValue<?>> shardingValues, List<String> shardingKeys) {
        List<String> keys = new ArrayList<>();

        for (String shardingKey : shardingKeys) {
            for (ShardingValue<?> each : shardingValues) {
                if (each.getColumnName().equalsIgnoreCase(shardingKey)) {
                    keys.add(shardingKey);
                }
            }
        }

        return keys;
    }

}
