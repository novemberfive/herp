package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetLocation;
import com.asset.repository.AssetLocationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 存放位置服务类
 */
@Service
public class AssetLocationService {

    private final AssetLocationMapper assetLocationMapper;

    public AssetLocationService(AssetLocationMapper assetLocationMapper) {
        this.assetLocationMapper = assetLocationMapper;
    }

    /**
     * 查询所有位置列表
     */
    @Cacheable(value = "location:list")
    public Result<List<AssetLocation>> getLocationList() {
        List<AssetLocation> locations = assetLocationMapper.selectAll();
        return Result.success(locations);
    }

    /**
     * 根据父级 ID 查询子位置
     */
    @Cacheable(value = "location:children", key = "#parentId")
    public Result<List<AssetLocation>> getLocationByParentId(Long parentId) {
        List<AssetLocation> locations = assetLocationMapper.selectByParentId(parentId);
        return Result.success(locations);
    }

    /**
     * 查询一级位置
     */
    @Cacheable(value = "location:toplevel")
    public Result<List<AssetLocation>> getTopLevelLocations() {
        List<AssetLocation> locations = assetLocationMapper.selectTopLevel();
        return Result.success(locations);
    }

    /**
     * 创建位置
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"location:list", "location:toplevel"}, allEntries = true)
    public Result<Void> createLocation(AssetLocation location) {
        // 设置默认层级
        if (location.getParentId() == null || location.getParentId() == 0) {
            location.setLevel(1);
        } else {
            AssetLocation parent = assetLocationMapper.selectById(location.getParentId());
            if (parent != null) {
                location.setLevel(parent.getLevel() + 1);
            } else {
                location.setLevel(1);
            }
        }
        
        assetLocationMapper.insert(location);
        return Result.success("位置创建成功", null);
    }

    /**
     * 更新位置
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"location:list", "location:toplevel", "location:children"}, allEntries = true)
    public Result<Void> updateLocation(AssetLocation location) {
        AssetLocation existing = assetLocationMapper.selectById(location.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("位置不存在");
        }
        
        assetLocationMapper.updateById(location);
        return Result.success("位置更新成功", null);
    }

    /**
     * 删除位置（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"location:list", "location:toplevel", "location:children"}, allEntries = true)
    public Result<Void> deleteLocation(Long id) {
        AssetLocation location = assetLocationMapper.selectById(id);
        if (location == null || location.getDeleted() == 1) {
            return Result.error("位置不存在");
        }
        
        // 检查是否有子位置
        LambdaQueryWrapper<AssetLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetLocation::getParentId, id);
        wrapper.eq(AssetLocation::getDeleted, 0);
        Long count = assetLocationMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("该位置下存在子位置，无法删除");
        }
        
        location.setDeleted(1);
        assetLocationMapper.updateById(location);
        return Result.success("位置删除成功", null);
    }
}
