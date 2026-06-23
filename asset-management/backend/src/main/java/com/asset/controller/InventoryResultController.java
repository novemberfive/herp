package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.InventoryResult;
import com.asset.service.InventoryResultService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 盘点结果控制器
 */
@RestController
@RequestMapping("/inventory/result")
public class InventoryResultController {

    private final InventoryResultService inventoryResultService;

    public InventoryResultController(InventoryResultService inventoryResultService) {
        this.inventoryResultService = inventoryResultService;
    }

    /**
     * 分页查询盘点结果列表
     * GET /api/inventory/result/list?pageNum=1&pageSize=10&taskId=1&resultType=1&reviewStatus=1
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('inventory:result')")
    public Result<Map<String, Object>> getResultList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer resultType,
            @RequestParam(required = false) Integer reviewStatus) {
        return inventoryResultService.getResultList(pageNum, pageSize, taskId, resultType, reviewStatus);
    }

    /**
     * 根据 ID 查询盘点结果详情
     * GET /api/inventory/result/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('inventory:result')")
    public Result<InventoryResult> getResultById(@PathVariable Long id) {
        return inventoryResultService.getResultById(id);
    }

    /**
     * 创建盘点结果
     * POST /api/inventory/result
     */
    @PostMapping
    @PreAuthorize("hasAuthority('inventory:result:create')")
    public Result<Void> createResult(@Valid @RequestBody InventoryResult result) {
        return inventoryResultService.createResult(result);
    }

    /**
     * 更新盘点结果
     * PUT /api/inventory/result
     */
    @PutMapping
    @PreAuthorize("hasAuthority('inventory:result:edit')")
    public Result<Void> updateResult(@Valid @RequestBody InventoryResult result) {
        return inventoryResultService.updateResult(result);
    }

    /**
     * 删除盘点结果
     * DELETE /api/inventory/result/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('inventory:result:delete')")
    public Result<Void> deleteResult(@PathVariable Long id) {
        return inventoryResultService.deleteResult(id);
    }

    /**
     * 提交盘点结果
     * POST /api/inventory/result/{id}/submit
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('inventory:result:submit')")
    public Result<Void> submitResult(
            @PathVariable Long id,
            @RequestParam Long inventoryUserId,
            @RequestParam String inventoryUserName) {
        return inventoryResultService.submitResult(id, inventoryUserId, inventoryUserName);
    }

    /**
     * 复核盘点结果
     * POST /api/inventory/result/{id}/review
     */
    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('inventory:result:review')")
    public Result<Void> reviewResult(
            @PathVariable Long id,
            @RequestParam Long reviewerId,
            @RequestParam String reviewerName,
            @RequestParam Integer reviewStatus,
            @RequestParam(required = false) String reviewRemark) {
        return inventoryResultService.reviewResult(id, reviewerId, reviewerName, reviewStatus, reviewRemark);
    }

    /**
     * 处理盘点结果
     * POST /api/inventory/result/{id}/process
     */
    @PostMapping("/{id}/process")
    @PreAuthorize("hasAuthority('inventory:result:process')")
    public Result<Void> processResult(
            @PathVariable Long id,
            @RequestParam Long processUserId,
            @RequestParam String processUserName,
            @RequestParam(required = false) String processRemark) {
        return inventoryResultService.processResult(id, processUserId, processUserName, processRemark);
    }

    /**
     * 批量导入盘点结果
     * POST /api/inventory/result/batch-import?taskId=1
     */
    @PostMapping("/batch-import")
    @PreAuthorize("hasAuthority('inventory:result:import')")
    public Result<Void> batchImportResults(
            @RequestParam Long taskId,
            @RequestBody List<InventoryResult> results) {
        return inventoryResultService.batchImportResults(taskId, results);
    }
}
