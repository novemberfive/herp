package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetCategory;
import com.asset.repository.AssetCategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 资产分类服务类
 */
@Service
public class AssetCategoryService {

    private final AssetCategoryMapper assetCategoryMapper;

    public AssetCategoryService(AssetCategoryMapper assetCategoryMapper) {
        this.assetCategoryMapper = assetCategoryMapper;
    }

    /**
     * 查询所有分类列表
     */
    @Cacheable(value = "category:list")
    public Result<List<AssetCategory>> getCategoryList() {
        List<AssetCategory> categories = assetCategoryMapper.selectAll();
        return Result.success(categories);
    }

    /**
     * 根据父级 ID 查询子分类
     */
    @Cacheable(value = "category:children", key = "#parentId")
    public Result<List<AssetCategory>> getCategoryByParentId(Long parentId) {
        List<AssetCategory> categories = assetCategoryMapper.selectByParentId(parentId);
        return Result.success(categories);
    }

    /**
     * 查询一级分类
     */
    @Cacheable(value = "category:toplevel")
    public Result<List<AssetCategory>> getTopLevelCategories() {
        List<AssetCategory> categories = assetCategoryMapper.selectTopLevel();
        return Result.success(categories);
    }

    /**
     * 创建分类
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"category:list", "category:toplevel"}, allEntries = true)
    public Result<Void> createCategory(AssetCategory category) {
        // 设置默认层级
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            AssetCategory parent = assetCategoryMapper.selectById(category.getParentId());
            if (parent != null) {
                category.setLevel(parent.getLevel() + 1);
            } else {
                category.setLevel(1);
            }
        }
        
        assetCategoryMapper.insert(category);
        return Result.success("分类创建成功", null);
    }

    /**
     * 更新分类
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"category:list", "category:toplevel", "category:children"}, allEntries = true)
    public Result<Void> updateCategory(AssetCategory category) {
        AssetCategory existing = assetCategoryMapper.selectById(category.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("分类不存在");
        }
        
        assetCategoryMapper.updateById(category);
        return Result.success("分类更新成功", null);
    }

    /**
     * 删除分类（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"category:list", "category:toplevel", "category:children"}, allEntries = true)
    public Result<Void> deleteCategory(Long id) {
        AssetCategory category = assetCategoryMapper.selectById(id);
        if (category == null || category.getDeleted() == 1) {
            return Result.error("分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCategory::getParentId, id);
        wrapper.eq(AssetCategory::getDeleted, 0);
        Long count = assetCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            return Result.error("该分类下存在子分类，无法删除");
        }
        
        category.setDeleted(1);
        assetCategoryMapper.updateById(category);
        return Result.success("分类删除成功", null);
    }
}
