package com.example.demo.dao;

import com.example.demo.model.po.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
	
    int deleteUserById(String id);

    int insertUser(User record);

    User findUser(User record);

    int updateUser(User record);
    
    int countUser(User record);
    
    List<User> findUserList(User record);

    String procUserLoginLog(Map record);

    Integer procUpdateUserErrorCount(Map record);

}