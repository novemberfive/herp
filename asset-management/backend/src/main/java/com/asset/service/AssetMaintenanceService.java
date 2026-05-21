package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetMaintenance;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 资产维修服务接口
 */
public interface AssetMaintenanceService {

    /**
     * 分页查询维修列表
     */
    Result<Map<String, Object>> getMaintenanceList(Integer pageNum, Integer pageSize, Integer maintenanceResult, Integer approveStatus);

    /**
     * 根据 ID 查询维修详情
     */
    Result<AssetMaintenance> getMaintenanceById(Long id);

    /**
     * 创建维修申请
     */
    Result<Void> createMaintenance(AssetMaintenance maintenance);

    /**
     * 更新维修信息
     */
    Result<Void> updateMaintenance(AssetMaintenance maintenance);

    /**
     * 删除维修记录（逻辑删除）
     */
    Result<Void> deleteMaintenance(Long id);

    /**
     * 审批维修申请
     */
    Result<Void> approveMaintenance(Long id, Integer approveStatus, String approveRemark, Long approverId);

    /**
     * 开始维修
     */
    Result<Void> startMaintenance(Long id, Long maintainerId, String maintainerName);

    /**
     * 完成维修
     */
    Result<Void> completeMaintenance(Long id, Integer maintenanceResult, String maintenanceMethod, BigDecimal maintenanceCost);

    /**
     * 取消维修
     */
    Result<Void> cancelMaintenance(Long id);
}
