package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.SysRole;
import com.asset.service.SysRoleService;
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
 * 系统角色控制器
 */
@RestController
@RequestMapping("/roles")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<List<SysRole>> getRoleList() {
        return sysRoleService.getRoleList();
    }

    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<List<SysRole>> getEnabledRoleList() {
        return sysRoleService.getEnabledRoleList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    public Result<Void> createRole(@RequestBody SysRole role) {
        return sysRoleService.createRole(role);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> updateRole(@RequestBody SysRole role) {
        return sysRoleService.updateRole(role);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        return sysRoleService.deleteRole(id);
    }
}
