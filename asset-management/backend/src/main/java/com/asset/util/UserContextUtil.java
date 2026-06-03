package com.asset.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户上下文工具类
 */
public class UserContextUtil {

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * 检查是否已登录
     */
    public static boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
            && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
