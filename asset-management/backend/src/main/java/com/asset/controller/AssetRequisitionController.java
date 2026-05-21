package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetRequisition;
import com.asset.service.AssetRequisitionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 资产领用退库控制器
 */
@RestController
@RequestMapping("/management/requisition")
public class AssetRequisitionController {
    
    private final AssetRequisitionService assetRequisitionService;
    
    public AssetRequisitionController(AssetRequisitionService assetRequisitionService) {
        this.assetRequisitionService = assetRequisitionService;
    }
    
    /**
     * 分页查询资产领用退库列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<PageResult<AssetRequisition>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            AssetRequisition query) {
        return assetRequisitionService.getPageList(pageNum, pageSize, query);
    }
    
    /**
     * 根据 ID 查询资产领用退库
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<AssetRequisition> getById(@PathVariable Long id) {
        return assetRequisitionService.getById(id);
    }
    
    /**
     * 创建资产领用退库
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> create(@Valid @RequestBody AssetRequisition requisition) {
        return assetRequisitionService.create(requisition);
    }
    
    /**
     * 更新资产领用退库
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> update(@Valid @RequestBody AssetRequisition requisition) {
        return assetRequisitionService.update(requisition);
    }
    
    /**
     * 删除资产领用退库
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return assetRequisitionService.delete(id);
    }
    
    /**
     * 提交审批
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> submit(@PathVariable Long id) {
        return assetRequisitionService.submit(id);
    }
    
    /**
     * 审批通过
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> approve(@PathVariable Long id, @RequestParam String opinion) {
        return assetRequisitionService.approve(id, opinion);
    }
    
    /**
     * 审批拒绝
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Result<Void> reject(@PathVariable Long id, @RequestParam String opinion) {
        return assetRequisitionService.reject(id, opinion);
    }
}
