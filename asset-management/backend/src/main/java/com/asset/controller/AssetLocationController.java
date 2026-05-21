package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetLocation;
import com.asset.service.AssetLocationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 存放位置控制器
 */
@RestController
@RequestMapping("/locations")
public class AssetLocationController {

    private final AssetLocationService assetLocationService;

    public AssetLocationController(AssetLocationService assetLocationService) {
        this.assetLocationService = assetLocationService;
    }

    /**
     * 查询所有位置列表
     * GET /api/locations/list
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetLocation>> getLocationList() {
        return assetLocationService.getLocationList();
    }

    /**
     * 根据父级 ID 查询子位置
     * GET /api/locations/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetLocation>> getLocationByParentId(@PathVariable Long parentId) {
        return assetLocationService.getLocationByParentId(parentId);
    }

    /**
     * 查询一级位置
     * GET /api/locations/top-level
     */
    @GetMapping("/top-level")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetLocation>> getTopLevelLocations() {
        return assetLocationService.getTopLevelLocations();
    }

    /**
     * 创建位置
     * POST /api/locations
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createLocation(@Valid @RequestBody AssetLocation location) {
        return assetLocationService.createLocation(location);
    }

    /**
     * 更新位置
     * PUT /api/locations
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateLocation(@Valid @RequestBody AssetLocation location) {
        return assetLocationService.updateLocation(location);
    }

    /**
     * 删除位置
     * DELETE /api/locations/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteLocation(@PathVariable Long id) {
        return assetLocationService.deleteLocation(id);
    }
}
