package com.asset.repository;

import com.asset.entity.AssetTransfer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产调拨 Mapper 接口
 */
@Mapper
public interface AssetTransferMapper extends BaseMapper<AssetTransfer> {
}
