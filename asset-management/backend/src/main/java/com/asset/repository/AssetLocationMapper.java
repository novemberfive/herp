package com.asset.repository;

import com.asset.entity.AssetLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 存放位置 Mapper 接口
 */
@Mapper
public interface AssetLocationMapper extends BaseMapper<AssetLocation> {

    /**
     * 查询所有位置（按层级排序）
     */
    @Select("SELECT * FROM asset_location WHERE deleted = 0 ORDER BY level, sort_order")
    List<AssetLocation> selectAll();

    /**
     * 根据父级 ID 查询子位置
     */
    @Select("SELECT * FROM asset_location WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<AssetLocation> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询一级位置
     */
    @Select("SELECT * FROM asset_location WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order")
    List<AssetLocation> selectTopLevel();
}
