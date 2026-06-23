package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetChangeRecord;
import com.asset.repository.AssetChangeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/changes")
public class AssetChangeRecordController {
    private final AssetChangeRecordMapper mapper;

    public AssetChangeRecordController(AssetChangeRecordMapper mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('archive:change')")
    public Result<PageResult<AssetChangeRecord>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String assetCode,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) Integer changeType) {
        Page<AssetChangeRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetChangeRecord> query = new LambdaQueryWrapper<>();
        query.eq(AssetChangeRecord::getDeleted, 0);
        if (assetCode != null && !assetCode.isBlank()) query.like(AssetChangeRecord::getAssetCode, assetCode.trim());
        if (assetName != null && !assetName.isBlank()) query.like(AssetChangeRecord::getAssetName, assetName.trim());
        if (changeType != null) query.eq(AssetChangeRecord::getChangeType, changeType);
        query.orderByDesc(AssetChangeRecord::getOperateTime).orderByDesc(AssetChangeRecord::getCreateTime);
        Page<AssetChangeRecord> result = mapper.selectPage(page, query);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal(), result.getSize(), result.getCurrent()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('archive:change')")
    public Result<AssetChangeRecord> detail(@PathVariable Long id) {
        return Result.success(mapper.selectById(id));
    }
}
