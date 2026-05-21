package com.asset.controller;

import com.asset.dto.Result;
import com.asset.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@NotBlank(message = "用户名不能为空") @RequestParam String username,
                                              @NotBlank(message = "密码不能为空") @RequestParam String password) {
        return authService.login(username, password);
    }

    /**
     * 刷新 Token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(@NotBlank(message = "Refresh Token 不能为空") @RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    /**
     * 获取当前用户信息
     * GET /api/auth/userinfo
     */
    @GetMapping("/userinfo")
    public Result<?> getUserInfo(@RequestAttribute("username") String username) {
        return authService.getUserInfo(username);
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return authService.logout();
    }
}
