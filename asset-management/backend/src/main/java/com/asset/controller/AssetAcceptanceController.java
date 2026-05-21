package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetAcceptance;
import com.asset.service.AssetAcceptanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 验收登记控制器
 */
@RestController
@RequestMapping("/acquisition/acceptance")
public class AssetAcceptanceController {
    
    private final AssetAcceptanceService assetAcceptanceService;
    
    public AssetAcceptanceController(AssetAcceptanceService assetAcceptanceService) {
        this.assetAcceptanceService = assetAcceptanceService;
    }
    
    /**
     * 分页查询验收登记列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<PageResult<AssetAcceptance>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            AssetAcceptance query) {
        return assetAcceptanceService.getPageList(pageNum, pageSize, query);
    }
    
    /**
     * 根据 ID 查询验收登记
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<AssetAcceptance> getById(@PathVariable Long id) {
        return assetAcceptanceService.getById(id);
    }
    
    /**
     * 创建验收登记
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> create(@Valid @RequestBody AssetAcceptance acceptance) {
        return assetAcceptanceService.create(acceptance);
    }
    
    /**
     * 更新验收登记
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> update(@Valid @RequestBody AssetAcceptance acceptance) {
        return assetAcceptanceService.update(acceptance);
    }
    
    /**
     * 删除验收登记
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        return assetAcceptanceService.delete(id);
    }
    
    /**
     * 提交验收
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> submit(@PathVariable Long id) {
        return assetAcceptanceService.submit(id);
    }
    
    /**
     * 验收通过
     */
    @PostMapping("/{id}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> accept(@PathVariable Long id, @RequestParam String opinion) {
        return assetAcceptanceService.accept(id, opinion);
    }
    
    /**
     * 验收拒绝
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> reject(@PathVariable Long id, @RequestParam String reason) {
        return assetAcceptanceService.reject(id, reason);
    }
}
