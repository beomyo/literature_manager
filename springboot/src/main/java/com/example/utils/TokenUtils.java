package com.example.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.common.Constants;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.impl.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {

    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    private static AdminService staticAdminService;

    @Resource
    private AdminService adminService;

    @PostConstruct
    public void setUserService() {
        staticAdminService = adminService;
    }

    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
                .sign(Algorithm.HMAC256(sign));
    }

    public static Account getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            if (ObjectUtil.isEmpty(token)) {
                log.warn("请求头中未找到 token");
                return new Account(); // 返回空对象，避免空指针
            }

            String userRole = JWT.decode(token).getAudience().get(0);
            String[] parts = userRole.split("-");
            if (parts.length != 2) {
                log.warn("Token 格式错误: {}", token);
                return new Account();
            }

            String userId = parts[0];
            String role = parts[1];

            if (RoleEnum.ADMIN.name().equals(role)) {
                Account account = staticAdminService.selectById(Integer.valueOf(userId));
                if (account == null) {
                    log.warn("管理员用户不存在，userId: {}", userId);
                    return new Account();
                }
                return account;
            }
            if (RoleEnum.USER.name().equals(role)) {
                Account account = staticAdminService.selectById(Integer.valueOf(userId));
                if (account == null) {
                    log.warn("用户不存在，userId: {}", userId);
                    return new Account();
                }
                return account;
            }
            // 如果需要支持 USER 角色，可添加对应逻辑
            log.warn("不支持的角色: {}", role);
            return new Account();
        } catch (Exception e) {
            log.error("获取当前用户信息出错", e);
            return new Account();
        }
    }

    public static String verifyToken(String token) {
        try {
            return JWT.decode(token).getAudience().get(0);
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            return null;
        }
    }
}