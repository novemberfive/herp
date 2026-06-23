package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.SysUser;
import com.asset.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统用户控制器
 */
@RestController
@RequestMapping("/users")
public class SysUserController {

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user')")
    public Result<List<SysUser>> getUserList() {
        return sysUserService.getUserList();
    }

    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('system:user')")
    public Result<List<SysUser>> getEnabledUserList() {
        return sysUserService.getEnabledUserList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    public Result<Void> createUser(@RequestBody SysUser user) {
        return sysUserService.createUser(user);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> updateUser(@RequestBody SysUser user) {
        return sysUserService.updateUser(user);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:reset-password')")
    public Result<Void> resetPassword(@PathVariable Long id) {
        return sysUserService.resetPassword(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        return sysUserService.deleteUser(id);
    }
}
