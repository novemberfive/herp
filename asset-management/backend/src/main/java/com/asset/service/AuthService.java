package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysUser;
import com.asset.repository.SysUserMapper;
import com.asset.util.JwtUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 */
@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    public AuthService(SysUserMapper sysUserMapper, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> login(String username, String password) {
        // 查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            return Result.error(403, "用户已被禁用");
        }

        // 校验密码
        if (!jwtUtil.checkPassword(password, user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole());

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", refreshToken);
        data.put("userInfo", user);

        return Result.success("登录成功", data);
    }

    /**
     * 刷新 Token
     */
    public Result<Map<String, String>> refreshToken(String refreshToken) {
        try {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            SysUser user = sysUserMapper.selectByUsername(username);
            
            if (user == null || user.getStatus() != 1) {
                return Result.error(401, "用户不存在或已被禁用");
            }

            String newToken = jwtUtil.generateToken(user.getUsername(), user.getRole());
            
            Map<String, String> data = new HashMap<>();
            data.put("token", newToken);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(401, "刷新 Token 失败");
        }
    }

    /**
     * 获取当前用户信息
     */
    @Cacheable(value = "user", key = "#username")
    public Result<SysUser> getUserInfo(String username) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        
        return Result.success(user);
    }

    /**
     * 用户登出
     */
    public Result<Void> logout() {
        // 轻量级版本，服务端不维护 session，客户端删除 token 即可
        return Result.success("登出成功", null);
    }
}
