package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.InventoryPlan;
import com.asset.service.InventoryPlanService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 盘点计划控制器
 */
@RestController
@RequestMapping("/inventory/plan")
public class InventoryPlanController {

    private final InventoryPlanService inventoryPlanService;

    public InventoryPlanController(InventoryPlanService inventoryPlanService) {
        this.inventoryPlanService = inventoryPlanService;
    }

    /**
     * 分页查询盘点计划列表
     * GET /api/inventory/plan/list?pageNum=1&pageSize=10&status=1&planType=2
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getPlanList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer planType) {
        return inventoryPlanService.getPlanList(pageNum, pageSize, status, planType);
    }

    /**
     * 根据 ID 查询盘点计划详情
     * GET /api/inventory/plan/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<InventoryPlan> getPlanById(@PathVariable Long id) {
        return inventoryPlanService.getPlanById(id);
    }

    /**
     * 创建盘点计划
     * POST /api/inventory/plan
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createPlan(@Valid @RequestBody InventoryPlan plan) {
        return inventoryPlanService.createPlan(plan);
    }

    /**
     * 更新盘点计划
     * PUT /api/inventory/plan
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updatePlan(@Valid @RequestBody InventoryPlan plan) {
        return inventoryPlanService.updatePlan(plan);
    }

    /**
     * 删除盘点计划
     * DELETE /api/inventory/plan/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deletePlan(@PathVariable Long id) {
        return inventoryPlanService.deletePlan(id);
    }

    /**
     * 启用盘点计划
     * POST /api/inventory/plan/{id}/enable
     */
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> enablePlan(@PathVariable Long id) {
        return inventoryPlanService.enablePlan(id);
    }

    /**
     * 停用盘点计划
     * POST /api/inventory/plan/{id}/disable
     */
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> disablePlan(@PathVariable Long id) {
        return inventoryPlanService.disablePlan(id);
    }

    /**
     * 执行盘点计划（生成盘点任务）
     * POST /api/inventory/plan/{id}/execute
     */
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> executePlan(@PathVariable Long id) {
        return inventoryPlanService.executePlan(id);
    }
}
