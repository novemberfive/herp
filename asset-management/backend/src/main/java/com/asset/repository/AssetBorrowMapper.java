package com.asset.repository;

import com.asset.entity.AssetBorrow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产借用 Mapper 接口
 */
@Mapper
public interface AssetBorrowMapper extends BaseMapper<AssetBorrow> {
}
