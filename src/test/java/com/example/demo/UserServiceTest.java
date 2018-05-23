package com.example.demo;

import com.example.demo.base.BaseTest;
import com.example.demo.db.sharding.ShardingTableRule;
import com.example.demo.model.po.User;
import com.example.demo.service.UserService;
import com.example.demo.util.KeyGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * Created by liunanhua on 2018/3/27.
 */

public class UserServiceTest extends BaseTest {

    @Autowired
    UserService userService;


    @Test
    public void test1() {
        User record = new User();
        record.setId("CF54F85D3AF54F1FBCCC148E2B6010F4");
        record = userService.findUser(record);
        System.out.println("=============================================================");
        System.out.println("logicTableName= "+ ShardingTableRule.generateTableName("t_user","CF54F85D3AF54F1FBCCC148E2B6010F4"));
        System.out.println(record);
    }

    @Test
    public void test2() {
        User record = new User();
        record.setName("a");
        List list = userService.findUserList(record);
        System.out.println("=============================================================");
        System.out.println(list);
    }

    @Test
    public void test3() {
        String uuid = KeyGenerator.getUUID();
        System.out.println("uuid= "+ uuid);
        User record = new User();
        record.setId(uuid);
        record.setName("a");
        record.setPassword("111111");
        int insertUser = userService.insertUser(record);
        System.out.println("=============================================================");
        System.out.println("logicTableName= "+ ShardingTableRule.generateTableName("t_user",uuid));
        System.out.println(insertUser);

    }


}
