package com.asset.service;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetRequisition;

/**
 * 资产领用退库服务接口
 */
public interface AssetRequisitionService {
    
    /**
     * 分页查询资产领用退库列表
     */
    Result<PageResult<AssetRequisition>> getPageList(Integer pageNum, Integer pageSize, AssetRequisition query);
    
    /**
     * 根据 ID 查询资产领用退库
     */
    Result<AssetRequisition> getById(Long id);
    
    /**
     * 创建资产领用退库
     */
    Result<Void> create(AssetRequisition requisition);
    
    /**
     * 更新资产领用退库
     */
    Result<Void> update(AssetRequisition requisition);
    
    /**
     * 删除资产领用退库
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
