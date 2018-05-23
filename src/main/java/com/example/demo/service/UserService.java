package com.example.demo.service;

import com.example.demo.db.DataSourceNames;
import com.example.demo.db.TargetDataSource;
import com.example.demo.dao.UserMapper;
import com.example.demo.model.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/8.
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public int deleteUserById(String id){
        return userMapper.deleteUserById(id);
    }

    @TargetDataSource(DataSourceNames.SHARDING_RP_WRITE)
    public int insertUser(User record){
        return userMapper.insertUser(record);
    }

    @TargetDataSource(DataSourceNames.DB_READ)
    public User findUser(User record){
        return userMapper.findUser(record);
    }

    public int updateUser(User record){
        return userMapper.updateUser(record);
    }

    public int countUser(User record){
        return userMapper.countUser(record);
    }

    @TargetDataSource(DataSourceNames.DB_READ)
    public List<User> findUserList(User record){
        return userMapper.findUserList(record);
    }

    public String procUserLoginLog(Map record){
        return userMapper.procUserLoginLog(record);
    }

    public Integer procUpdateUserErrorCount(Map record){
        return userMapper.procUpdateUserErrorCount(record);
    }

}
