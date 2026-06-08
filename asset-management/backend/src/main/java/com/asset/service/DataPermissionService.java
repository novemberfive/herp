package com.asset.service;

import com.asset.entity.AssetCard;
import com.asset.entity.SysUser;
import com.asset.repository.SysUserMapper;
import com.asset.util.UserContextUtil;
import org.springframework.stereotype.Service;

/**
 * 轻量级部门数据权限服务。
 */
@Service
public class DataPermissionService {

    private final SysUserMapper sysUserMapper;

    public DataPermissionService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public SysUser getCurrentUser() {
        String username = UserContextUtil.getCurrentUsername();
        if (username == null) {
            return null;
        }
        return sysUserMapper.selectByUsername(username);
    }

    public boolean isAdmin(SysUser user) {
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    public boolean shouldRestrictToOwnDepartment() {
        SysUser currentUser = getCurrentUser();
        return currentUser != null && !isAdmin(currentUser);
    }

    public Long getCurrentDepartmentId() {
        SysUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getDepartmentId() : null;
    }

    public Long getCurrentUserId() {
        SysUser currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }

    public Long resolveDepartmentScope(Long requestedDepartmentId) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return requestedDepartmentId;
        }
        if (isAdmin(currentUser)) {
            return requestedDepartmentId;
        }
        return currentUser.getDepartmentId();
    }

    public boolean canAccessDepartment(Long departmentId) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        if (isAdmin(currentUser)) {
            return true;
        }
        return departmentId != null && departmentId.equals(currentUser.getDepartmentId());
    }

    public boolean canAccessCreatedBy(Long createUserId) {
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        if (isAdmin(currentUser)) {
            return true;
        }
        return createUserId != null && createUserId.equals(currentUser.getId());
    }

    public boolean canAccessAsset(AssetCard asset) {
        if (asset == null) {
            return false;
        }
        SysUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        if (isAdmin(currentUser)) {
            return true;
        }
        Long departmentId = currentUser.getDepartmentId();
        return departmentId != null && departmentId.equals(asset.getDepartmentId());
    }
}
