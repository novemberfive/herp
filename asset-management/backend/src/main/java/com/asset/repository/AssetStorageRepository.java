package com.asset.repository;

import com.asset.entity.AssetStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产入库数据访问层
 */
@Mapper
public interface AssetStorageRepository extends BaseMapper<AssetStorage> {
}
