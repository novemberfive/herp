package com.asset.service;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.PurchaseRequest;

/**
 * 采购申请服务接口
 */
public interface PurchaseRequestService {
    
    /**
     * 分页查询采购申请列表
     */
    Result<PageResult<PurchaseRequest>> getPageList(Integer pageNum, Integer pageSize, PurchaseRequest query);
    
    /**
     * 根据 ID 查询采购申请
     */
    Result<PurchaseRequest> getById(Long id);
    
    /**
     * 创建采购申请
     */
    Result<Void> create(PurchaseRequest request);
    
    /**
     * 更新采购申请
     */
    Result<Void> update(PurchaseRequest request);
    
    /**
     * 删除采购申请
     */
    Result<Void> delete(Long id);
    
    /**
     * 提交审批
     */
    Result<Void> submit(Long id);
    
    /**
     * 审批通过
     */
    Result<Void> approve(Long id, String opinion);
    
    /**
     * 审批拒绝
     */
    Result<Void> reject(Long id, String opinion);
}
