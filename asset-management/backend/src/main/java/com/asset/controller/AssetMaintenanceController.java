package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetMaintenance;
import com.asset.service.AssetMaintenanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 资产维修控制器
 */
@RestController
@RequestMapping("/maintenance")
public class AssetMaintenanceController {

    private final AssetMaintenanceService assetMaintenanceService;

    public AssetMaintenanceController(AssetMaintenanceService assetMaintenanceService) {
        this.assetMaintenanceService = assetMaintenanceService;
    }

    /**
     * 分页查询维修列表
     * GET /api/maintenance/list?pageNum=1&pageSize=10&maintenanceResult=2&approveStatus=1
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('archive:maintenance')")
    public Result<Map<String, Object>> getMaintenanceList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer maintenanceResult,
            @RequestParam(required = false) Integer approveStatus) {
        return assetMaintenanceService.getMaintenanceList(pageNum, pageSize, maintenanceResult, approveStatus);
    }

    /**
     * 根据 ID 查询维修详情
     * GET /api/maintenance/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('archive:maintenance')")
    public Result<AssetMaintenance> getMaintenanceById(@PathVariable Long id) {
        return assetMaintenanceService.getMaintenanceById(id);
    }

    /**
     * 创建维修申请
     * POST /api/maintenance
     */
    @PostMapping
    @PreAuthorize("hasAuthority('archive:maintenance:create')")
    public Result<Void> createMaintenance(@Valid @RequestBody AssetMaintenance maintenance) {
        return assetMaintenanceService.createMaintenance(maintenance);
    }

    /**
     * 更新维修信息
     * PUT /api/maintenance
     */
    @PutMapping
    @PreAuthorize("hasAuthority('archive:maintenance:edit')")
    public Result<Void> updateMaintenance(@Valid @RequestBody AssetMaintenance maintenance) {
        return assetMaintenanceService.updateMaintenance(maintenance);
    }

    /**
     * 删除维修记录
     * DELETE /api/maintenance/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('archive:maintenance:delete')")
    public Result<Void> deleteMaintenance(@PathVariable Long id) {
        return assetMaintenanceService.deleteMaintenance(id);
    }

    /**
     * 审批维修申请
     * POST /api/maintenance/{id}/approve
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('archive:maintenance:approve')")
    public Result<Void> approveMaintenance(
            @PathVariable Long id,
            @RequestParam Integer approveStatus,
            @RequestParam(required = false) String approveRemark,
            @RequestParam Long approverId) {
        return assetMaintenanceService.approveMaintenance(id, approveStatus, approveRemark, approverId);
    }

    /**
     * 开始维修
     * POST /api/maintenance/{id}/start
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('archive:maintenance:start')")
    public Result<Void> startMaintenance(
            @PathVariable Long id,
            @RequestParam Long maintainerId,
            @RequestParam String maintainerName) {
        return assetMaintenanceService.startMaintenance(id, maintainerId, maintainerName);
    }

    /**
     * 完成维修
     * POST /api/maintenance/{id}/complete
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('archive:maintenance:complete')")
    public Result<Void> completeMaintenance(
            @PathVariable Long id,
            @RequestParam Integer maintenanceResult,
            @RequestParam String maintenanceMethod,
            @RequestParam(required = false) BigDecimal maintenanceCost) {
        return assetMaintenanceService.completeMaintenance(id, maintenanceResult, maintenanceMethod, maintenanceCost);
    }

    /**
     * 取消维修
     * POST /api/maintenance/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('archive:maintenance:cancel')")
    public Result<Void> cancelMaintenance(@PathVariable Long id) {
        return assetMaintenanceService.cancelMaintenance(id);
    }
}
