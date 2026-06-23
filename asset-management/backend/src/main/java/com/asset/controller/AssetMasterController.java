package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetMaster;
import com.asset.service.AssetMasterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资产主数据控制器
 */
@RestController
@RequestMapping("/asset-masters")
public class AssetMasterController {

    private final AssetMasterService assetMasterService;

    public AssetMasterController(AssetMasterService assetMasterService) {
        this.assetMasterService = assetMasterService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('basic:asset-master')")
    public Result<List<AssetMaster>> getAssetMasterList() {
        return assetMasterService.getAssetMasterList();
    }

    @GetMapping("/enabled")
    @PreAuthorize("hasAuthority('basic:asset-master')")
    public Result<List<AssetMaster>> getEnabledAssetMasterList() {
        return assetMasterService.getEnabledAssetMasterList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('basic:asset-master:create')")
    public Result<Void> createAssetMaster(@RequestBody AssetMaster assetMaster) {
        return assetMasterService.createAssetMaster(assetMaster);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('basic:asset-master:edit')")
    public Result<Void> updateAssetMaster(@RequestBody AssetMaster assetMaster) {
        return assetMasterService.updateAssetMaster(assetMaster);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('basic:asset-master:status')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return assetMasterService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('basic:asset-master:delete')")
    public Result<Void> deleteAssetMaster(@PathVariable Long id) {
        return assetMasterService.deleteAssetMaster(id);
    }
}
