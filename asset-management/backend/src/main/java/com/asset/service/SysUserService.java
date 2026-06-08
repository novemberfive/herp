package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysDepartment;
import com.asset.entity.SysRole;
import com.asset.entity.SysUser;
import com.asset.repository.SysRoleMapper;
import com.asset.repository.SysDepartmentMapper;
import com.asset.repository.SysUserMapper;
import com.asset.util.JwtUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统用户服务类
 */
@Service
public class SysUserService {

    private static final String DEFAULT_PASSWORD = "admin123";

    private final SysUserMapper sysUserMapper;
    private final SysDepartmentMapper sysDepartmentMapper;
    private final SysRoleMapper sysRoleMapper;
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;

    public SysUserService(SysUserMapper sysUserMapper,
                          SysDepartmentMapper sysDepartmentMapper,
                          SysRoleMapper sysRoleMapper,
                          JwtUtil jwtUtil,
                          OperationLogService operationLogService) {
        this.sysUserMapper = sysUserMapper;
        this.sysDepartmentMapper = sysDepartmentMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.jwtUtil = jwtUtil;
        this.operationLogService = operationLogService;
    }

    @Cacheable(value = "user:list")
    public Result<List<SysUser>> getUserList() {
        List<SysUser> users = sysUserMapper.selectAllWithDepartment();
        users.forEach(this::sanitizeUser);
        return Result.success(users);
    }

    @Cacheable(value = "user:enabled")
    public Result<List<SysUser>> getEnabledUserList() {
        List<SysUser> users = sysUserMapper.selectEnabledWithDepartment();
        users.forEach(this::sanitizeUser);
        return Result.success(users);
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> createUser(SysUser user) {
        String validationMessage = validateUser(user, false);
        if (validationMessage != null) {
            return Result.error(validationMessage);
        }

        fillDepartmentInfo(user);
        fillDefaults(user, true);
        sysUserMapper.insert(user);
        Result<Void> result = Result.success("用户创建成功", null);
        operationLogService.record("SYSTEM", "CREATE_USER", "sys_user", String.valueOf(user.getId()), "创建用户：" + user.getUsername(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> updateUser(SysUser user) {
        if (user.getId() == null) {
            return Result.error("用户 ID 不能为空");
        }

        SysUser existing = sysUserMapper.selectById(user.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("用户不存在");
        }

        String validationMessage = validateUser(user, true);
        if (validationMessage != null) {
            return Result.error(validationMessage);
        }

        fillDepartmentInfo(user);
        fillDefaults(user, false);
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(jwtUtil.encodePassword(user.getPassword().trim()));
        }
        sysUserMapper.updateById(user);
        Result<Void> result = Result.success("用户更新成功", null);
        operationLogService.record("SYSTEM", "UPDATE_USER", "sys_user", String.valueOf(user.getId()), "更新用户：" + user.getUsername(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> resetPassword(Long id) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("用户不存在");
        }

        existing.setPassword(jwtUtil.encodePassword(DEFAULT_PASSWORD));
        sysUserMapper.updateById(existing);
        Result<Void> result = Result.success("密码已重置为 " + DEFAULT_PASSWORD, null);
        operationLogService.record("SYSTEM", "RESET_USER_PASSWORD", "sys_user", String.valueOf(existing.getId()), "重置用户密码：" + existing.getUsername(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"user:list", "user:enabled", "user"}, allEntries = true)
    public Result<Void> deleteUser(Long id) {
        SysUser existing = sysUserMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("用户不存在");
        }
        if ("admin".equals(existing.getUsername())) {
            return Result.error("默认管理员账号不允许删除");
        }

        existing.setDeleted(1);
        sysUserMapper.updateById(existing);
        Result<Void> result = Result.success("用户删除成功", null);
        operationLogService.record("SYSTEM", "DELETE_USER", "sys_user", String.valueOf(existing.getId()), "删除用户：" + existing.getUsername(), result);
        return result;
    }

    private String validateUser(SysUser user, boolean updating) {
        if (!StringUtils.hasText(user.getUsername())) {
            return "用户名不能为空";
        }
        if (!updating && !StringUtils.hasText(user.getPassword())) {
            return "初始密码不能为空";
        }
        if (!StringUtils.hasText(user.getRealName())) {
            return "真实姓名不能为空";
        }
        if (!StringUtils.hasText(user.getRole())) {
            return "角色不能为空";
        }

        user.setUsername(user.getUsername().trim());
        user.setRealName(user.getRealName().trim());
        user.setRole(user.getRole().trim().toLowerCase());
        if (StringUtils.hasText(user.getPhone())) {
            user.setPhone(user.getPhone().trim());
        }
        if (StringUtils.hasText(user.getEmail())) {
            user.setEmail(user.getEmail().trim());
        }

        SysRole role = sysRoleMapper.selectByRoleCode(user.getRole());
        if (role == null) {
            return "角色不存在";
        }
        if (role.getStatus() != null && role.getStatus() != 1) {
            return "角色已被禁用";
        }

        SysUser sameUsername = sysUserMapper.selectByUsername(user.getUsername());
        if (sameUsername != null && (!updating || !sameUsername.getId().equals(user.getId()))) {
            return "用户名已存在";
        }

        if (StringUtils.hasText(user.getPhone())) {
            SysUser samePhone = sysUserMapper.selectByPhone(user.getPhone());
            if (samePhone != null && (!updating || !samePhone.getId().equals(user.getId()))) {
                return "手机号已存在";
            }
        }

        if (StringUtils.hasText(user.getEmail())) {
            SysUser sameEmail = sysUserMapper.selectByEmail(user.getEmail());
            if (sameEmail != null && (!updating || !sameEmail.getId().equals(user.getId()))) {
                return "邮箱已存在";
            }
        }

        if (user.getDepartmentId() != null) {
            SysDepartment department = sysDepartmentMapper.selectById(user.getDepartmentId());
            if (department == null || department.getDeleted() == 1) {
                return "所属部门不存在";
            }
            if (department.getStatus() != null && department.getStatus() != 1) {
                return "所属部门已被禁用";
            }
        }

        return null;
    }

    private void fillDepartmentInfo(SysUser user) {
        if (user.getDepartmentId() == null) {
            user.setDepartmentName(null);
            return;
        }

        SysDepartment department = sysDepartmentMapper.selectById(user.getDepartmentId());
        user.setDepartmentName(department == null ? null : department.getDeptName());
    }

    private void fillDefaults(SysUser user, boolean creating) {
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (creating) {
            user.setPassword(jwtUtil.encodePassword(user.getPassword().trim()));
        }
    }

    private void sanitizeUser(SysUser user) {
        user.setPassword(null);
    }
}
