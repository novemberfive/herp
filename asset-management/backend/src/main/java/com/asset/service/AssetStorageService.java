package com.asset.service;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetStorage;

/**
 * 资产入库服务接口
 */
public interface AssetStorageService {
    
    /**
     * 分页查询资产入库列表
     */
    Result<PageResult<AssetStorage>> getPageList(Integer pageNum, Integer pageSize, AssetStorage query);
    
    /**
     * 根据 ID 查询资产入库
     */
    Result<AssetStorage> getById(Long id);
    
    /**
     * 创建资产入库
     */
    Result<Void> create(AssetStorage storage);
    
    /**
     * 更新资产入库
     */
    Result<Void> update(AssetStorage storage);
    
    /**
     * 删除资产入库
     */
    Result<Void> delete(Long id);
    
    /**
     * 确认入库
     */
    Result<Void> confirm(Long id);
}
