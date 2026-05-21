package com.asset.service.impl;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetStorage;
import com.asset.repository.AssetStorageRepository;
import com.asset.service.AssetStorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 资产入库服务实现类
 */
@Service
public class AssetStorageServiceImpl implements AssetStorageService {
    
    private final AssetStorageRepository assetStorageRepository;
    
    public AssetStorageServiceImpl(AssetStorageRepository assetStorageRepository) {
        this.assetStorageRepository = assetStorageRepository;
    }
    
    @Override
    public Result<PageResult<AssetStorage>> getPageList(Integer pageNum, Integer pageSize, AssetStorage query) {
        Page<AssetStorage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetStorage> wrapper = new LambdaQueryWrapper<>();
        
        if (query != null) {
            if (query.getStorageNo() != null && !query.getStorageNo().isEmpty()) {
                wrapper.like(AssetStorage::getStorageNo, query.getStorageNo());
            }
            if (query.getAcceptanceNo() != null && !query.getAcceptanceNo().isEmpty()) {
                wrapper.like(AssetStorage::getAcceptanceNo, query.getAcceptanceNo());
            }
            if (query.getPurchaseRequestNo() != null && !query.getPurchaseRequestNo().isEmpty()) {
                wrapper.like(AssetStorage::getPurchaseRequestNo, query.getPurchaseRequestNo());
            }
            if (query.getAssetName() != null && !query.getAssetName().isEmpty()) {
                wrapper.like(AssetStorage::getAssetName, query.getAssetName());
            }
            if (query.getCategoryId() != null) {
                wrapper.eq(AssetStorage::getCategoryId, query.getCategoryId());
            }
            if (query.getStatus() != null) {
                wrapper.eq(AssetStorage::getStatus, query.getStatus());
            }
            if (query.getStorageType() != null) {
                wrapper.eq(AssetStorage::getStorageType, query.getStorageType());
            }
        }
        
        wrapper.eq(AssetStorage::getDeleted, 0);
        wrapper.orderByDesc(AssetStorage::getCreateTime);
        
        Page<AssetStorage> resultPage = assetStorageRepository.selectPage(page, wrapper);
        PageResult<AssetStorage> pageResult = new PageResult<>(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getSize(),
            resultPage.getCurrent()
        );
        
        return Result.success(pageResult);
    }
    
    @Override
    public Result<AssetStorage> getById(Long id) {
        AssetStorage storage = assetStorageRepository.selectById(id);
        if (storage == null || storage.getDeleted() == 1) {
            return Result.error("资产入库记录不存在");
        }
        return Result.success(storage);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(AssetStorage storage) {
        storage.setStorageNo(generateStorageNo());
        storage.setStorageDate(LocalDateTime.now());
        if (storage.getStatus() == null) {
            storage.setStatus(0); // 待入库
        }
        assetStorageRepository.insert(storage);
        return Result.success("资产入库创建成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(AssetStorage storage) {
        AssetStorage existing = assetStorageRepository.selectById(storage.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("资产入库记录不存在");
        }
        if (existing.getStatus() == 1) {
            return Result.error("已入库的记录不能修改");
        }
        storage.setUpdateTime(LocalDateTime.now());
        assetStorageRepository.updateById(storage);
        return Result.success("资产入库更新成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(Long id) {
        AssetStorage storage = assetStorageRepository.selectById(id);
        if (storage == null || storage.getDeleted() == 1) {
            return Result.error("资产入库记录不存在");
        }
        if (storage.getStatus() == 1) {
            return Result.error("已入库的记录不能删除");
        }
        storage.setDeleted(1);
        assetStorageRepository.updateById(storage);
        return Result.success("资产入库记录删除成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> confirm(Long id) {
        AssetStorage storage = assetStorageRepository.selectById(id);
        if (storage == null || storage.getDeleted() == 1) {
            return Result.error("资产入库记录不存在");
        }
        if (storage.getStatus() == 1) {
            return Result.error("该记录已经入库");
        }
        
        storage.setStatus(1); // 已入库
        storage.setUpdateTime(LocalDateTime.now());
        assetStorageRepository.updateById(storage);
        return Result.success("资产入库确认成功", null);
    }
    
    private String generateStorageNo() {
        return "ST" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
