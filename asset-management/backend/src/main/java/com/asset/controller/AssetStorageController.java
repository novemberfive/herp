package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetStorage;
import com.asset.service.AssetStorageService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 资产入库控制器
 */
@RestController
@RequestMapping("/acquisition/storage")
public class AssetStorageController {
    
    private final AssetStorageService assetStorageService;
    
    public AssetStorageController(AssetStorageService assetStorageService) {
        this.assetStorageService = assetStorageService;
    }
    
    /**
     * 分页查询资产入库列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('acquisition:storage')")
    public Result<PageResult<AssetStorage>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            AssetStorage query) {
        return assetStorageService.getPageList(pageNum, pageSize, query);
    }
    
    /**
     * 根据 ID 查询资产入库
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:storage')")
    public Result<AssetStorage> getById(@PathVariable Long id) {
        return assetStorageService.getById(id);
    }
    
    /**
     * 创建资产入库
     */
    @PostMapping
    @PreAuthorize("hasAuthority('acquisition:storage:create')")
    public Result<Void> create(@Valid @RequestBody AssetStorage storage) {
        return assetStorageService.create(storage);
    }
    
    /**
     * 更新资产入库
     */
    @PutMapping
    @PreAuthorize("hasAuthority('acquisition:storage:edit')")
    public Result<Void> update(@Valid @RequestBody AssetStorage storage) {
        return assetStorageService.update(storage);
    }
    
    /**
     * 删除资产入库
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:storage:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return assetStorageService.delete(id);
    }
    
    /**
     * 确认入库
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('acquisition:storage:confirm')")
    public Result<Void> confirm(@PathVariable Long id) {
        return assetStorageService.confirm(id);
    }
}
