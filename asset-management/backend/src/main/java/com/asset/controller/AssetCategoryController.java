package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetCategory;
import com.asset.service.AssetCategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类控制器
 */
@RestController
@RequestMapping("/categories")
public class AssetCategoryController {

    private final AssetCategoryService assetCategoryService;

    public AssetCategoryController(AssetCategoryService assetCategoryService) {
        this.assetCategoryService = assetCategoryService;
    }

    /**
     * 查询所有分类列表
     * GET /api/categories/list
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetCategory>> getCategoryList() {
        return assetCategoryService.getCategoryList();
    }

    /**
     * 根据父级 ID 查询子分类
     * GET /api/categories/parent/{parentId}
     */
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetCategory>> getCategoryByParentId(@PathVariable Long parentId) {
        return assetCategoryService.getCategoryByParentId(parentId);
    }

    /**
     * 查询一级分类
     * GET /api/categories/top-level
     */
    @GetMapping("/top-level")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<List<AssetCategory>> getTopLevelCategories() {
        return assetCategoryService.getTopLevelCategories();
    }

    /**
     * 创建分类
     * POST /api/categories
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createCategory(@Valid @RequestBody AssetCategory category) {
        return assetCategoryService.createCategory(category);
    }

    /**
     * 更新分类
     * PUT /api/categories
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateCategory(@Valid @RequestBody AssetCategory category) {
        return assetCategoryService.updateCategory(category);
    }

    /**
     * 删除分类
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        return assetCategoryService.deleteCategory(id);
    }
}
