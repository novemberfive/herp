package com.asset.repository;

import com.asset.entity.AssetMaster;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资产主数据 Mapper 接口
 */
@Mapper
public interface AssetMasterMapper extends BaseMapper<AssetMaster> {

    @Select("""
        SELECT am.*,
               ac.category_name AS categoryName
        FROM asset_master am
        LEFT JOIN asset_category ac ON ac.id = am.category_id
        WHERE am.deleted = 0
        ORDER BY am.status DESC, am.asset_code, am.id
        """)
    List<AssetMaster> selectAllWithCategory();

    @Select("""
        SELECT am.*,
               ac.category_name AS categoryName
        FROM asset_master am
        LEFT JOIN asset_category ac ON ac.id = am.category_id
        WHERE am.deleted = 0
          AND am.status = 1
        ORDER BY am.asset_code, am.id
        """)
    List<AssetMaster> selectEnabledWithCategory();

    @Select("""
        SELECT * FROM asset_master
        WHERE deleted = 0
          AND asset_code = #{assetCode}
        LIMIT 1
        """)
    AssetMaster selectByAssetCode(@Param("assetCode") String assetCode);
}
