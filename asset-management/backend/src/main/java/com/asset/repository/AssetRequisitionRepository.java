package com.asset.repository;

import com.asset.entity.AssetRequisition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产领用退库数据访问层
 */
@Mapper
public interface AssetRequisitionRepository extends BaseMapper<AssetRequisition> {
}
