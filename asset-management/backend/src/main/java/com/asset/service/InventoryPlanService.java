package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.InventoryPlan;

import java.util.Map;

/**
 * 盘点计划服务接口
 */
public interface InventoryPlanService {

    /**
     * 分页查询盘点计划列表
     */
    Result<Map<String, Object>> getPlanList(Integer pageNum, Integer pageSize, Integer status, Integer planType);

    /**
     * 根据 ID 查询盘点计划详情
     */
    Result<InventoryPlan> getPlanById(Long id);

    /**
     * 创建盘点计划
     */
    Result<Void> createPlan(InventoryPlan plan);

    /**
     * 更新盘点计划
     */
    Result<Void> updatePlan(InventoryPlan plan);

    /**
     * 删除盘点计划（逻辑删除）
     */
    Result<Void> deletePlan(Long id);

    /**
     * 启用盘点计划
     */
    Result<Void> enablePlan(Long id);

    /**
     * 停用盘点计划
     */
    Result<Void> disablePlan(Long id);

    /**
     * 执行盘点计划（生成盘点任务）
     */
    Result<Void> executePlan(Long id);
}
