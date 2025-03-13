package com.example.service;

import com.example.entity.Account;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 登录
     */
    Account login(Account account);

    /**
     * 注册
     */
    void register(Account account);

    /**
     * 根据ID查询用户
     */
    Account selectById(Integer id);

    /**
     * 更新用户信息
     */
    void updateById(Account account);

    /**
     * 修改密码
     */
    void updatePassword(Account account);
}