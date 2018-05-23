package com.example.demo.db.sharding;

import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.ShardingDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liunanhua on 2018/5/9.
 */
public interface ShardingDataSourceCollector {

    Map<String, ShardingDataSource> collectShardingDataSources();

    ShardingDataSourceCollector NO_OP = new NoOpShardingDataSourceCollector();


    class NoOpShardingDataSourceCollector implements ShardingDataSourceCollector {

        @Override
        public Map<String, ShardingDataSource> collectShardingDataSources() {
            return new HashMap<>();
        }
    }

}
