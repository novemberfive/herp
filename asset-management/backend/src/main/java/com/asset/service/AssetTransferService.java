package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetTransfer;

import java.util.Map;

/**
 * 资产调拨服务接口
 */
public interface AssetTransferService {

    /**
     * 分页查询调拨列表
     */
    Result<Map<String, Object>> getTransferList(Integer pageNum, Integer pageSize, Integer approveStatus, Integer transferStatus);

    /**
     * 根据 ID 查询调拨详情
     */
    Result<AssetTransfer> getTransferById(Long id);

    /**
     * 创建调拨申请
     */
    Result<Void> createTransfer(AssetTransfer transfer);

    /**
     * 更新调拨信息
     */
    Result<Void> updateTransfer(AssetTransfer transfer);

    /**
     * 删除调拨记录（逻辑删除）
     */
    Result<Void> deleteTransfer(Long id);

    /**
     * 审批调拨申请
     */
    Result<Void> approveTransfer(Long id, Integer approveStatus, String approveRemark, Long approverId);

    /**
     * 完成调拨
     */
    Result<Void> completeTransfer(Long id);

    /**
     * 取消调拨
     */
    Result<Void> cancelTransfer(Long id);
}
