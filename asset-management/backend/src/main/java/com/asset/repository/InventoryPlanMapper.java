package com.asset.repository;

import com.asset.entity.InventoryPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 盘点计划 Mapper 接口
 */
@Mapper
public interface InventoryPlanMapper extends BaseMapper<InventoryPlan> {
}
