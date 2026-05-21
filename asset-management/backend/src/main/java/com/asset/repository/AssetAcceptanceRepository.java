package com.asset.repository;

import com.asset.entity.AssetAcceptance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验收登记数据访问层
 */
@Mapper
public interface AssetAcceptanceRepository extends BaseMapper<AssetAcceptance> {
}
