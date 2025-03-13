package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.impl.AdminService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @Resource
    private AdminService adminService;
    
    @Resource
    private UserService userService;
    
    @GetMapping("/")
    public Result<String> hello() {
        return Result.success("访问成功");
    }
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Account> login(@RequestBody Account account) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        try {
            Account loginAccount = null;
            if (RoleEnum.ADMIN.name().equals(account.getRole())) {
                loginAccount = adminService.login(account);
            } else if (RoleEnum.USER.name().equals(account.getRole())) {
                loginAccount = userService.login(account);
            }
            if (loginAccount == null) {
                return Result.error(ResultCodeEnum.USER_ACCOUNT_ERROR);
            }
            return Result.success(loginAccount);
        } catch (CustomException e) {
            return Result.error(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return Result.error(ResultCodeEnum.SYSTEM_ERROR);
        }
    }
    
    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.register(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        }
        return Result.success();
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

}
