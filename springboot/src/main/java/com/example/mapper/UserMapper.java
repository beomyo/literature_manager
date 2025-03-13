package com.example.mapper;

import com.example.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     */
    Account selectByUsername(String username);

    /**
     * 根据ID查询用户
     */
    Account selectById(Integer id);

    /**
     * 插入用户数据
     */
    void insert(Account account);

    /**
     * 更新用户信息
     */
    void updateById(Account account);
}