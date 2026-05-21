package com.asset.repository;

import com.asset.entity.AssetCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资产分类 Mapper 接口
 */
@Mapper
public interface AssetCategoryMapper extends BaseMapper<AssetCategory> {

    /**
     * 查询所有分类（按层级排序）
     */
    @Select("SELECT * FROM asset_category WHERE deleted = 0 ORDER BY level, sort_order")
    List<AssetCategory> selectAll();

    /**
     * 根据父级 ID 查询子分类
     */
    @Select("SELECT * FROM asset_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<AssetCategory> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询一级分类
     */
    @Select("SELECT * FROM asset_category WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order")
    List<AssetCategory> selectTopLevel();
}
