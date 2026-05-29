package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.InventoryResult;

import java.util.Map;

/**
 * 盘点结果服务接口
 */
public interface InventoryResultService {

    /**
     * 分页查询盘点结果列表
     */
    Result<Map<String, Object>> getResultList(Integer pageNum, Integer pageSize, Long taskId, Integer resultType, Integer reviewStatus);

    /**
     * 根据 ID 查询盘点结果详情
     */
    Result<InventoryResult> getResultById(Long id);

    /**
     * 创建盘点结果
     */
    Result<Void> createResult(InventoryResult result);

    /**
     * 更新盘点结果
     */
    Result<Void> updateResult(InventoryResult result);

    /**
     * 删除盘点结果（逻辑删除）
     */
    Result<Void> deleteResult(Long id);

    /**
     * 提交盘点结果
     */
    Result<Void> submitResult(Long id, Long inventoryUserId, String inventoryUserName);

    /**
     * 复核盘点结果
     */
    Result<Void> reviewResult(Long id, Long reviewerId, String reviewerName, Integer reviewStatus, String reviewRemark);

    /**
     * 处理盘点结果
     */
    Result<Void> processResult(Long id, Long processUserId, String processUserName, String processRemark);

    /**
     * 批量导入盘点结果
     */
    Result<Void> batchImportResults(Long taskId, java.util.List<InventoryResult> results);
}
