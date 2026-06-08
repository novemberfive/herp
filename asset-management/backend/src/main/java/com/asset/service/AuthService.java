package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysRole;
import com.asset.entity.SysUser;
import com.asset.repository.SysRoleMapper;
import com.asset.repository.SysUserMapper;
import com.asset.util.JwtUtil;
import com.asset.util.PermissionUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务类
 */
@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;

    public AuthService(SysUserMapper sysUserMapper,
                       SysRoleMapper sysRoleMapper,
                       JwtUtil jwtUtil,
                       OperationLogService operationLogService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.jwtUtil = jwtUtil;
        this.operationLogService = operationLogService;
    }

    /**
     * 用户登录
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> login(String username, String password) {
        // 查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            Result<Map<String, Object>> result = Result.error(401, "用户名或密码错误");
            operationLogService.record("AUTH", "LOGIN", "sys_user", username, "用户名密码登录失败", result);
            return result;
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            Result<Map<String, Object>> result = Result.error(403, "用户已被禁用");
            operationLogService.record("AUTH", "LOGIN", "sys_user", String.valueOf(user.getId()), "登录失败，用户已禁用", result);
            return result;
        }

        // 校验密码
        if (!jwtUtil.checkPassword(password, user.getPassword())) {
            Result<Map<String, Object>> result = Result.error(401, "用户名或密码错误");
            operationLogService.record("AUTH", "LOGIN", "sys_user", String.valueOf(user.getId()), "用户名密码登录失败", result);
            return result;
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        List<String> permissions = getRolePermissions(user.getRole());

        // 生成 Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), permissions);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole(), permissions);

        // 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("refreshToken", refreshToken);
        data.put("userInfo", buildUserInfo(user));

        Result<Map<String, Object>> result = Result.success("登录成功", data);
        operationLogService.record("AUTH", "LOGIN", "sys_user", String.valueOf(user.getId()), "用户登录成功", result);
        return result;
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

            String newToken = jwtUtil.generateToken(user.getUsername(), user.getRole(), getRolePermissions(user.getRole()));
            
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

        return Result.success(buildUserInfo(user));
    }

    /**
     * 用户登出
     */
    public Result<Void> logout() {
        // 轻量级版本，服务端不维护 session，客户端删除 token 即可
        Result<Void> result = Result.success("登出成功", null);
        operationLogService.record("AUTH", "LOGOUT", "sys_user", null, "用户主动登出", result);
        return result;
    }

    private SysUser buildUserInfo(SysUser user) {
        user.setPassword(null);

        SysRole role = sysRoleMapper.selectByRoleCode(user.getRole());
        if (role != null) {
            user.setRoleName(role.getRoleName());
            user.setPermissions(PermissionUtil.parseAndExpand(role.getPermissions()));
        } else {
            user.setPermissions(List.of());
        }
        return user;
    }

    private List<String> getRolePermissions(String roleCode) {
        SysRole role = sysRoleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            return List.of();
        }
        return PermissionUtil.parseAndExpand(role.getPermissions());
    }
}
