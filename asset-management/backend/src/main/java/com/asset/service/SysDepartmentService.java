package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysDepartment;
import com.asset.repository.SysDepartmentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统部门服务类
 */
@Service
public class SysDepartmentService {

    private final SysDepartmentMapper sysDepartmentMapper;
    private final OperationLogService operationLogService;

    public SysDepartmentService(SysDepartmentMapper sysDepartmentMapper, OperationLogService operationLogService) {
        this.sysDepartmentMapper = sysDepartmentMapper;
        this.operationLogService = operationLogService;
    }

    /**
     * 查询所有部门列表
     */
    @Cacheable(value = "department:list")
    public Result<List<SysDepartment>> getDepartmentList() {
        List<SysDepartment> departments = sysDepartmentMapper.selectAll();
        return Result.success(departments);
    }

    /**
     * 根据父级 ID 查询子部门
     */
    @Cacheable(value = "department:children", key = "#parentId")
    public Result<List<SysDepartment>> getDepartmentByParentId(Long parentId) {
        List<SysDepartment> departments = sysDepartmentMapper.selectByParentId(parentId);
        return Result.success(departments);
    }

    /**
     * 查询一级部门
     */
    @Cacheable(value = "department:toplevel")
    public Result<List<SysDepartment>> getTopLevelDepartments() {
        List<SysDepartment> departments = sysDepartmentMapper.selectTopLevel();
        return Result.success(departments);
    }

    /**
     * 创建部门
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"department:list", "department:toplevel", "department:children"}, allEntries = true)
    public Result<Void> createDepartment(SysDepartment department) {
        if (department.getParentId() == null || department.getParentId() == 0) {
            department.setLevel(1);
        } else {
            SysDepartment parent = sysDepartmentMapper.selectById(department.getParentId());
            if (parent != null && parent.getDeleted() == 0) {
                department.setLevel(parent.getLevel() + 1);
            } else {
                department.setLevel(1);
                department.setParentId(0L);
            }
        }

        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }

        sysDepartmentMapper.insert(department);
        Result<Void> result = Result.success("部门创建成功", null);
        operationLogService.record("BASIC", "CREATE_DEPARTMENT", "sys_department", String.valueOf(department.getId()), "创建部门：" + department.getDeptName(), result);
        return result;
    }

    /**
     * 更新部门
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"department:list", "department:toplevel", "department:children"}, allEntries = true)
    public Result<Void> updateDepartment(SysDepartment department) {
        SysDepartment existing = sysDepartmentMapper.selectById(department.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("部门不存在");
        }

        if (department.getParentId() != null && department.getParentId().equals(department.getId())) {
            return Result.error("父级部门不能选择自身");
        }

        if (department.getParentId() == null || department.getParentId() == 0) {
            department.setLevel(1);
            department.setParentId(0L);
        } else {
            SysDepartment parent = sysDepartmentMapper.selectById(department.getParentId());
            if (parent == null || parent.getDeleted() == 1) {
                return Result.error("父级部门不存在");
            }
            department.setLevel(parent.getLevel() + 1);
        }

        sysDepartmentMapper.updateById(department);
        Result<Void> result = Result.success("部门更新成功", null);
        operationLogService.record("BASIC", "UPDATE_DEPARTMENT", "sys_department", String.valueOf(department.getId()), "更新部门：" + department.getDeptName(), result);
        return result;
    }

    /**
     * 删除部门（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"department:list", "department:toplevel", "department:children"}, allEntries = true)
    public Result<Void> deleteDepartment(Long id) {
        SysDepartment department = sysDepartmentMapper.selectById(id);
        if (department == null || department.getDeleted() == 1) {
            return Result.error("部门不存在");
        }

        LambdaQueryWrapper<SysDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDepartment::getParentId, id);
        wrapper.eq(SysDepartment::getDeleted, 0);
        Long count = sysDepartmentMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("该部门下存在子部门，无法删除");
        }

        department.setDeleted(1);
        sysDepartmentMapper.updateById(department);
        Result<Void> result = Result.success("部门删除成功", null);
        operationLogService.record("BASIC", "DELETE_DEPARTMENT", "sys_department", String.valueOf(department.getId()), "删除部门：" + department.getDeptName(), result);
        return result;
    }
}
