package com.example.demo.db.sharding;

import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.example.demo.db.DataSourceNames;
import com.example.demo.db.sharding.read.ReadShardingInfo;
import com.example.demo.db.sharding.write.ShardingInfo;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 写库分离
 */
public class RecordShardingDataSourceFactoryBean extends ShardingDataSourceFactoryBean {

    private Boolean isWrite ;

    public RecordShardingDataSourceFactoryBean(boolean isWrite){
        this.isWrite = isWrite;
    }

    @Override
    protected ShardingRule createShardingRule() {
        DataSourceRule dataSourceRule = new DataSourceRule(createDataSourceMap());
        ShardingTableRule recordTable = new ShardingTableRule(DataSourceNames.DB_SHARDING_TABLE_BASE_NAME);
        //ShardingTableRule recordTable2 = new ShardingTableRule("t_redpacket");

        TableRule recordTableRule = TableRule.builder(recordTable.getLogicTableName()).actualTables(recordTable.getActualTableNames()).dataSourceRule(dataSourceRule).build();
        //TableRule recordTableRule2 = TableRule.builder(recordTable2.getLogicTableName()).actualTables(recordTable2.getActualTableNames()).dataSourceRule(dataSourceRule).build();

        ShardingRule shardingRule = null;
        if (this.isWrite){
            ShardingInfo shardingInfo = ShardingInfo.getInstance();

            /*shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(recordTableRule,recordTableRule2))
                .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(recordTableRule,recordTableRule2))))
                .tableShardingStrategy(new TableShardingStrategy(shardingInfo.shardingKeys, new TableShardingAlgorithm())).build();*/
            shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(recordTableRule))
                    .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(recordTableRule))))
                    .tableShardingStrategy(new TableShardingStrategy(shardingInfo.shardingKeys, new TableShardingAlgorithm(this.isWrite,shardingInfo,null))).build();
        }else {
            ReadShardingInfo readShardingInfo = ReadShardingInfo.getInstance();

            /*shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(recordTableRule,recordTableRule2))
                .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(recordTableRule,recordTableRule2))))
                .tableShardingStrategy(new TableShardingStrategy(shardingInfo.shardingKeys, new ReadTableShardingAlgorithm())).build();*/
            shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(recordTableRule))
                    .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(recordTableRule))))
                    .tableShardingStrategy(new TableShardingStrategy(readShardingInfo.shardingKeys, new TableShardingAlgorithm(this.isWrite,null,readShardingInfo))).build();
        }
        return shardingRule;
    }

    protected Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<String, DataSource>();
        if (this.isWrite){
            result.put(DataSourceNames.DB_WRITE, (DataSource)beanFactory.getBean(DataSourceNames.DB_WRITE, DataSource.class));
        }else {
            result.put(DataSourceNames.DB_READ, (DataSource)beanFactory.getBean(DataSourceNames.DB_READ, DataSource.class));
        }
        return result;
    }


}
