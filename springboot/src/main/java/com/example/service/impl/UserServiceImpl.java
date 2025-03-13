package com.example.service.impl;

import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户服务实现类
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TokenUtils tokenUtils;

    @Override
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    @Override
    public void register(Account account) {
        // 参数校验
        if (account == null || account.getUsername() == null || account.getPassword() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        // 检查用户名是否已存在
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser != null) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        // 设置用户角色
        account.setRole(RoleEnum.USER.name());
        userMapper.insert(account);
    }

    @Override
    public Account selectById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateById(Account account) {
        userMapper.updateById(account);
    }

    @Override
    public void updatePassword(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (dbUser == null) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }
}