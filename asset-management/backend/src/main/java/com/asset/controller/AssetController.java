package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetCard;
import com.asset.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产管理控制器
 */
@RestController
@RequestMapping("/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * 分页查询资产列表
     * GET /api/assets/list?pageNum=1&pageSize=10&status=1
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getAssetList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return assetService.getAssetList(pageNum, pageSize, status);
    }

    /**
     * 根据 ID 查询资产详情
     * GET /api/assets/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<AssetCard> getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }

    /**
     * 创建资产
     * POST /api/assets
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createAsset(@Valid @RequestBody AssetCard asset) {
        return assetService.createAsset(asset);
    }

    /**
     * 更新资产
     * PUT /api/assets
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateAsset(@Valid @RequestBody AssetCard asset) {
        return assetService.updateAsset(asset);
    }

    /**
     * 删除资产
     * DELETE /api/assets/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteAsset(@PathVariable Long id) {
        return assetService.deleteAsset(id);
    }

    /**
     * 获取资产统计信息
     * GET /api/assets/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getStats() {
        return assetService.getStats();
    }
}
