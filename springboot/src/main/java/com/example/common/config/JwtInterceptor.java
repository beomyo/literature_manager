// E:\manager\springboot\src\main\java\com\example\common\config\JwtInterceptor.java
package com.example.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.service.impl.AdminService;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取 token
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            // 如果请求头中没有 token，从参数中再尝试获取
            token = request.getParameter(Constants.TOKEN);
        }

        // 如果 token 仍然为空，抛出异常
        if (ObjectUtil.isEmpty(token)) {
            log.warn("请求中未携带 token，URI: {}", request.getRequestURI());
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }

        Account account = null;
        try {
            // 解析 token 获取用户角色和 ID
            String userRole = JWT.decode(token).getAudience().get(0);
            String[] parts = userRole.split("-");
            if (parts.length != 2) {
                log.warn("Token 格式错误: {}", token);
                throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
            }
            String userId = parts[0];
            String role = parts[1];

            // 验证角色是否合法
            if (!RoleEnum.ADMIN.name().equals(role) && !RoleEnum.USER.name().equals(role)) {
                log.warn("无效的角色: {}", role);
                throw new CustomException(ResultCodeEnum.NO_AUTH_ERROR);
            }

            // 根据角色查询用户信息
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(userId));
            } else if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(userId));
            }

            // 检查用户是否存在
            if (ObjectUtil.isNull(account)) {
                log.warn("用户不存在，userId: {}, role: {}", userId, role);
                throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }

            // 使用用户密码验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            log.error("Token 验证失败: {}", token, e);
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        } catch (Exception e) {
            log.error("解析 token 时发生未知错误: {}", token, e);
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }

        return true;
    }
}