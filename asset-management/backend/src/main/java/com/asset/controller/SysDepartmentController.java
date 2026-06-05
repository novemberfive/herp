package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.SysDepartment;
import com.asset.service.SysDepartmentService;
import jakarta.validation.Valid;
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
 * 系统部门控制器
 */
@RestController
@RequestMapping("/departments")
public class SysDepartmentController {

    private final SysDepartmentService sysDepartmentService;

    public SysDepartmentController(SysDepartmentService sysDepartmentService) {
        this.sysDepartmentService = sysDepartmentService;
    }

    /**
     * 查询所有部门列表
     * GET /api/departments/list
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<SysDepartment>> getDepartmentList() {
        return sysDepartmentService.getDepartmentList();
    }

    /**
     * 根据父级 ID 查询子部门
     * GET /api/departments/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<SysDepartment>> getDepartmentByParentId(@PathVariable Long parentId) {
        return sysDepartmentService.getDepartmentByParentId(parentId);
    }

    /**
     * 查询一级部门
     * GET /api/departments/top-level
     */
    @GetMapping("/top-level")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<SysDepartment>> getTopLevelDepartments() {
        return sysDepartmentService.getTopLevelDepartments();
    }

    /**
     * 创建部门
     * POST /api/departments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createDepartment(@Valid @RequestBody SysDepartment department) {
        return sysDepartmentService.createDepartment(department);
    }

    /**
     * 更新部门
     * PUT /api/departments
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateDepartment(@Valid @RequestBody SysDepartment department) {
        return sysDepartmentService.updateDepartment(department);
    }

    /**
     * 删除部门
     * DELETE /api/departments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDepartment(@PathVariable Long id) {
        return sysDepartmentService.deleteDepartment(id);
    }
}
