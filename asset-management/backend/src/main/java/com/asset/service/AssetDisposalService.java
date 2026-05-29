package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetDisposal;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 资产处置服务接口
 */
public interface AssetDisposalService {

    /**
     * 分页查询资产处置列表
     */
    Result<Map<String, Object>> getDisposalList(Integer pageNum, Integer pageSize, Integer disposalType, Integer approveStatus, Integer disposalStatus);

    /**
     * 根据 ID 查询资产处置详情
     */
    Result<AssetDisposal> getDisposalById(Long id);

    /**
     * 创建资产处置申请
     */
    Result<Void> createDisposal(AssetDisposal disposal);

    /**
     * 更新资产处置申请
     */
    Result<Void> updateDisposal(AssetDisposal disposal);

    /**
     * 删除资产处置申请（逻辑删除）
     */
    Result<Void> deleteDisposal(Long id);

    /**
     * 审批资产处置申请
     */
    Result<Void> approveDisposal(Long id, Long approverId, String approverName, Integer approveStatus, String approveRemark);

    /**
     * 执行资产处置
     */
    Result<Void> executeDisposal(Long id, Integer disposalMethod, BigDecimal actualValue, String buyerName, String buyerContact);

    /**
     * 完成资产处置
     */
    Result<Void> completeDisposal(Long id);

    /**
     * 取消资产处置
     */
    Result<Void> cancelDisposal(Long id, String reason);
}
