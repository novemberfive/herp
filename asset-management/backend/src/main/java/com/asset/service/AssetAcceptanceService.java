package com.asset.service;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetAcceptance;

/**
 * 验收登记服务接口
 */
public interface AssetAcceptanceService {
    
    /**
     * 分页查询验收登记列表
     */
    Result<PageResult<AssetAcceptance>> getPageList(Integer pageNum, Integer pageSize, AssetAcceptance query);
    
    /**
     * 根据 ID 查询验收登记
     */
    Result<AssetAcceptance> getById(Long id);
    
    /**
     * 创建验收登记
     */
    Result<Void> create(AssetAcceptance acceptance);
    
    /**
     * 更新验收登记
     */
    Result<Void> update(AssetAcceptance acceptance);
    
    /**
     * 删除验收登记
     */
    Result<Void> delete(Long id);
    
    /**
     * 提交验收
     */
    Result<Void> submit(Long id);
    
    /**
     * 验收通过
     */
    Result<Void> accept(Long id, String opinion);
    
    /**
     * 验收拒绝
     */
    Result<Void> reject(Long id, String reason);
}
