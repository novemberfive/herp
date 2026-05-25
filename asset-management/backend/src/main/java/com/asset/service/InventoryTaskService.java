package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.InventoryTask;

import java.util.Map;

/**
 * 盘点任务服务接口
 */
public interface InventoryTaskService {

    /**
     * 分页查询盘点任务列表
     */
    Result<Map<String, Object>> getTaskList(Integer pageNum, Integer pageSize, Integer status, Integer taskType);

    /**
     * 根据 ID 查询盘点任务详情
     */
    Result<InventoryTask> getTaskById(Long id);

    /**
     * 创建盘点任务
     */
    Result<Void> createTask(InventoryTask task);

    /**
     * 更新盘点任务
     */
    Result<Void> updateTask(InventoryTask task);

    /**
     * 删除盘点任务（逻辑删除）
     */
    Result<Void> deleteTask(Long id);

    /**
     * 开始盘点任务
     */
    Result<Void> startTask(Long id);

    /**
     * 完成盘点任务
     */
    Result<Void> completeTask(Long id);

    /**
     * 终止盘点任务
     */
    Result<Void> cancelTask(Long id);
}
