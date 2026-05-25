package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.InventoryTask;
import com.asset.service.InventoryTaskService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 盘点任务控制器
 */
@RestController
@RequestMapping("/inventory/task")
public class InventoryTaskController {

    private final InventoryTaskService inventoryTaskService;

    public InventoryTaskController(InventoryTaskService inventoryTaskService) {
        this.inventoryTaskService = inventoryTaskService;
    }

    /**
     * 分页查询盘点任务列表
     * GET /api/inventory/task/list?pageNum=1&pageSize=10&status=1&taskType=2
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getTaskList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer taskType) {
        return inventoryTaskService.getTaskList(pageNum, pageSize, status, taskType);
    }

    /**
     * 根据 ID 查询盘点任务详情
     * GET /api/inventory/task/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<InventoryTask> getTaskById(@PathVariable Long id) {
        return inventoryTaskService.getTaskById(id);
    }

    /**
     * 创建盘点任务
     * POST /api/inventory/task
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createTask(@Valid @RequestBody InventoryTask task) {
        return inventoryTaskService.createTask(task);
    }

    /**
     * 更新盘点任务
     * PUT /api/inventory/task
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateTask(@Valid @RequestBody InventoryTask task) {
        return inventoryTaskService.updateTask(task);
    }

    /**
     * 删除盘点任务
     * DELETE /api/inventory/task/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteTask(@PathVariable Long id) {
        return inventoryTaskService.deleteTask(id);
    }

    /**
     * 开始盘点任务
     * POST /api/inventory/task/{id}/start
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> startTask(@PathVariable Long id) {
        return inventoryTaskService.startTask(id);
    }

    /**
     * 完成盘点任务
     * POST /api/inventory/task/{id}/complete
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> completeTask(@PathVariable Long id) {
        return inventoryTaskService.completeTask(id);
    }

    /**
     * 终止盘点任务
     * POST /api/inventory/task/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> cancelTask(@PathVariable Long id) {
        return inventoryTaskService.cancelTask(id);
    }
}
