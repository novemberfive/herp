package com.asset.repository;

import com.asset.entity.AssetChangeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产变更记录 Mapper 接口
 */
@Mapper
public interface AssetChangeRecordMapper extends BaseMapper<AssetChangeRecord> {
}
