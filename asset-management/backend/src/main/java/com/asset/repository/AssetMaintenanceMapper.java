package com.asset.repository;

import com.asset.entity.AssetMaintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产维修记录 Mapper 接口
 */
@Mapper
public interface AssetMaintenanceMapper extends BaseMapper<AssetMaintenance> {
}
