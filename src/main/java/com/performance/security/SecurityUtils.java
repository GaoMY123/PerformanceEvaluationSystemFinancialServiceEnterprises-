package com.performance.security;

import com.performance.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类 - 获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户实体
     */
    public static SysUser getCurrentUser() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUser() : null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        SysUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        SysUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}
