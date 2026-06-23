package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetBorrow;

import java.util.Map;

/**
 * 资产借用服务接口
 */
public interface AssetBorrowService {

    /**
     * 分页查询资产借用列表
     */
    Result<Map<String, Object>> getBorrowList(Integer pageNum, Integer pageSize, Integer borrowStatus,
                                              Long borrowerId, String borrowNo, String assetCode,
                                              String assetName, String borrowerName);

    /**
     * 根据 ID 查询资产借用详情
     */
    Result<AssetBorrow> getBorrowById(Long id);

    /**
     * 创建资产借用申请
     */
    Result<Void> createBorrow(AssetBorrow borrow);

    /**
     * 更新资产借用申请
     */
    Result<Void> updateBorrow(AssetBorrow borrow);

    /**
     * 删除资产借用申请（逻辑删除）
     */
    Result<Void> deleteBorrow(Long id);

    /**
     * 审批资产借用申请
     */
    Result<Void> approveBorrow(Long id, Long approverId, String approverName, Integer approveStatus, String approveRemark);

    /**
     * 归还资产
     */
    Result<Void> returnAsset(Long id, String returnCondition, String returnRemark);

    /**
     * 取消借用
     */
    Result<Void> cancelBorrow(Long id, String reason);

    /**
     * 发送逾期提醒
     */
    Result<Void> sendOverdueReminder(Long id);
}
