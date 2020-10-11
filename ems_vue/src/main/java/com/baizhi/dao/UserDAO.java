package com.baizhi.dao;


import com.baizhi.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper//创建DAO对象
public interface UserDAO {
    void save(User user);

    User findByUserName(String username);
}
