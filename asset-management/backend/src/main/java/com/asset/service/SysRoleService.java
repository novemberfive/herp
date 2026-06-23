package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysRole;
import com.asset.repository.SysRoleMapper;
import com.asset.repository.SysUserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色服务类
 */
@Service
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;
    private final OperationLogService operationLogService;

    public SysRoleService(SysRoleMapper sysRoleMapper,
                          SysUserMapper sysUserMapper,
                          OperationLogService operationLogService) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserMapper = sysUserMapper;
        this.operationLogService = operationLogService;
    }

    @Cacheable(value = "role:list")
    public Result<List<SysRole>> getRoleList() {
        return Result.success(sysRoleMapper.selectAll());
    }

    @Cacheable(value = "role:enabled")
    public Result<List<SysRole>> getEnabledRoleList() {
        return Result.success(sysRoleMapper.selectEnabled());
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"role:list", "role:enabled", "user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> createRole(SysRole role) {
        String validateMessage = validateRole(role, false);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(role);
        sysRoleMapper.insert(role);
        Result<Void> result = Result.success("角色创建成功", null);
        operationLogService.record("SYSTEM", "CREATE_ROLE", "sys_role", String.valueOf(role.getId()), "创建角色：" + role.getRoleCode(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"role:list", "role:enabled", "user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> updateRole(SysRole role) {
        if (role.getId() == null) {
            return Result.error("角色 ID 不能为空");
        }

        SysRole existing = sysRoleMapper.selectById(role.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("角色不存在");
        }

        String validateMessage = validateRole(role, true);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(role);
        sysRoleMapper.updateById(role);
        Result<Void> result = Result.success("角色更新成功", null);
        operationLogService.record("SYSTEM", "UPDATE_ROLE", "sys_role", String.valueOf(role.getId()), "更新角色：" + role.getRoleCode(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"role:list", "role:enabled", "user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> deleteRole(Long id) {
        SysRole existing = sysRoleMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("角色不存在");
        }
        if ("admin".equals(existing.getRoleCode())) {
            return Result.error("默认管理员角色不允许删除");
        }
        if (sysUserMapper.countByRole(existing.getRoleCode()) > 0) {
            return Result.error("该角色仍有用户在使用，无法删除");
        }

        existing.setDeleted(1);
        sysRoleMapper.updateById(existing);
        Result<Void> result = Result.success("角色删除成功", null);
        operationLogService.record("SYSTEM", "DELETE_ROLE", "sys_role", String.valueOf(existing.getId()), "删除角色：" + existing.getRoleCode(), result);
        return result;
    }

    private void fillDefaults(SysRole role) {
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        role.setPermissions(normalizePermissions(role.getPermissions()));
    }

    private String validateRole(SysRole role, boolean updating) {
        if (!StringUtils.hasText(role.getRoleName())) {
            return "角色名称不能为空";
        }
        if (!StringUtils.hasText(role.getRoleCode())) {
            return "角色编码不能为空";
        }

        role.setRoleName(role.getRoleName().trim());
        role.setRoleCode(role.getRoleCode().trim().toLowerCase());

        if (!role.getRoleCode().matches("[a-z][a-z0-9_]{1,19}")) {
            return "角色编码仅支持 2-20 位小写字母、数字和下划线，且需以字母开头";
        }

        SysRole sameName = sysRoleMapper.selectByRoleName(role.getRoleName());
        if (sameName != null && (!updating || !sameName.getId().equals(role.getId()))) {
            return "角色名称已存在";
        }

        SysRole sameCode = sysRoleMapper.selectByRoleCode(role.getRoleCode());
        if (sameCode != null && (!updating || !sameCode.getId().equals(role.getId()))) {
            return "角色编码已存在";
        }

        return null;
    }

    private String normalizePermissions(String permissions) {
        if (!StringUtils.hasText(permissions)) {
            return "";
        }
        return Arrays.stream(permissions.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.joining(","));
    }
}
