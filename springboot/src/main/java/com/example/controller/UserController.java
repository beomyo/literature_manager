package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Account> login(@RequestBody Account account) {
        Account loginUser = userService.login(account);
        return Result.success(loginUser);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Account account) {
        userService.register(account);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Account account) {
        userService.updatePassword(account);
        return Result.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Account account) {
        userService.updateById(account);
        return Result.success();
    }
}