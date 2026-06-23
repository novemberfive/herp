package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.AssetTransfer;
import com.asset.repository.AssetTransferMapper;
import com.asset.service.AssetTransferService;
import com.asset.service.DataPermissionService;
import com.asset.service.OperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 资产调拨服务实现类
 */
@Service
public class AssetTransferServiceImpl implements AssetTransferService {

    private final AssetTransferMapper assetTransferMapper;
    private final DataPermissionService dataPermissionService;
    private final OperationLogService operationLogService;

    public AssetTransferServiceImpl(AssetTransferMapper assetTransferMapper,
                                    DataPermissionService dataPermissionService,
                                    OperationLogService operationLogService) {
        this.assetTransferMapper = assetTransferMapper;
        this.dataPermissionService = dataPermissionService;
        this.operationLogService = operationLogService;
    }

    @Override
    public Result<Map<String, Object>> getTransferList(Integer pageNum, Integer pageSize, Integer approveStatus, Integer transferStatus) {
        Page<AssetTransfer> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetTransfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetTransfer::getDeleted, 0);
        Result<Map<String, Object>> scopeResult = applyDepartmentScope(wrapper);
        if (scopeResult != null) {
            return scopeResult;
        }
        
        if (approveStatus != null) {
            wrapper.eq(AssetTransfer::getApproveStatus, approveStatus);
        }
        
        if (transferStatus != null) {
            wrapper.eq(AssetTransfer::getTransferStatus, transferStatus);
        }
        
        wrapper.orderByDesc(AssetTransfer::getCreateTime);
        
        Page<AssetTransfer> resultPage = assetTransferMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<AssetTransfer> getTransferById(Long id) {
        AssetTransfer transfer = assetTransferMapper.selectById(id);
        if (transfer == null || transfer.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        if (!canAccessTransfer(transfer)) {
            return Result.forbidden("无权查看该调拨记录");
        }
        return Result.success(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createTransfer(AssetTransfer transfer) {
        // 生成调拨单号
        String transferCode = generateTransferCode();
        transfer.setTransferCode(transferCode);
        
        // 设置初始状态
        transfer.setApplyTime(LocalDateTime.now());
        transfer.setApproveStatus(0); // 待审批
        transfer.setTransferStatus(0); // 待调拨
        
        assetTransferMapper.insert(transfer);
        Result<Void> result = Result.success("调拨申请创建成功", null);
        operationLogService.record("MANAGEMENT", "CREATE_TRANSFER", "asset_transfer",
            String.valueOf(transfer.getId()), "创建调拨单：" + transfer.getTransferCode(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateTransfer(AssetTransfer transfer) {
        AssetTransfer existing = assetTransferMapper.selectById(transfer.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        
        // 只有待审批状态可以修改
        if (existing.getApproveStatus() != 0) {
            return Result.error("已审批的调拨申请不能修改");
        }
        
        assetTransferMapper.updateById(transfer);
        Result<Void> result = Result.success("调拨信息更新成功", null);
        operationLogService.record("MANAGEMENT", "UPDATE_TRANSFER", "asset_transfer",
            String.valueOf(transfer.getId()), "更新调拨单：" + existing.getTransferCode(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteTransfer(Long id) {
        AssetTransfer transfer = assetTransferMapper.selectById(id);
        if (transfer == null || transfer.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        
        transfer.setDeleted(1);
        assetTransferMapper.updateById(transfer);
        Result<Void> result = Result.success("调拨记录删除成功", null);
        operationLogService.record("MANAGEMENT", "DELETE_TRANSFER", "asset_transfer",
            String.valueOf(transfer.getId()), "删除调拨单：" + transfer.getTransferCode(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveTransfer(Long id, Integer approveStatus, String approveRemark, Long approverId) {
        AssetTransfer transfer = assetTransferMapper.selectById(id);
        if (transfer == null || transfer.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        
        if (transfer.getApproveStatus() != 0) {
            return Result.error("该调拨申请已审批过");
        }
        
        transfer.setApproveStatus(approveStatus);
        transfer.setApproverId(approverId);
        transfer.setApproveTime(LocalDateTime.now());
        transfer.setApproveRemark(approveRemark);
        
        // 如果审批通过，状态变为调拨中
        if (approveStatus == 1) {
            transfer.setTransferStatus(1);
        } else if (approveStatus == 2) {
            transfer.setTransferStatus(3); // 拒绝则取消
        }
        
        assetTransferMapper.updateById(transfer);
        Result<Void> result = Result.success("调拨申请审批完成", null);
        operationLogService.record("MANAGEMENT", approveStatus == 1 ? "APPROVE_TRANSFER" : "REJECT_TRANSFER",
            "asset_transfer", String.valueOf(transfer.getId()),
            (approveStatus == 1 ? "审批通过调拨单：" : "审批拒绝调拨单：") + transfer.getTransferCode(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeTransfer(Long id) {
        AssetTransfer transfer = assetTransferMapper.selectById(id);
        if (transfer == null || transfer.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        
        if (transfer.getApproveStatus() != 1) {
            return Result.error("只有通过审批的调拨申请才能完成");
        }
        
        transfer.setTransferStatus(2); // 已完成
        transfer.setCompleteTime(LocalDateTime.now());
        
        assetTransferMapper.updateById(transfer);
        Result<Void> result = Result.success("调拨已完成", null);
        operationLogService.record("MANAGEMENT", "COMPLETE_TRANSFER", "asset_transfer",
            String.valueOf(transfer.getId()), "完成调拨单：" + transfer.getTransferCode(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelTransfer(Long id) {
        AssetTransfer transfer = assetTransferMapper.selectById(id);
        if (transfer == null || transfer.getDeleted() == 1) {
            return Result.error("调拨记录不存在");
        }
        
        if (transfer.getTransferStatus() == 2) {
            return Result.error("已完成的调拨不能取消");
        }
        
        transfer.setTransferStatus(3); // 已取消
        assetTransferMapper.updateById(transfer);
        Result<Void> result = Result.success("调拨已取消", null);
        operationLogService.record("MANAGEMENT", "CANCEL_TRANSFER", "asset_transfer",
            String.valueOf(transfer.getId()), "取消调拨单：" + transfer.getTransferCode(), result);
        return result;
    }

    /**
     * 生成调拨单号
     * 规则：TR + 年月日时分秒 + 4 位随机数
     */
    private String generateTransferCode() {
        return "TR" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
               + String.format("%04d", (int)(Math.random() * 10000));
    }

    private Result<Map<String, Object>> applyDepartmentScope(LambdaQueryWrapper<AssetTransfer> wrapper) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return null;
        }
        Long departmentId = dataPermissionService.getCurrentDepartmentId();
        if (departmentId == null) {
            return Result.forbidden("当前用户未绑定部门，无法查看调拨记录");
        }
        wrapper.and(scope -> scope
            .eq(AssetTransfer::getFromDepartmentId, departmentId)
            .or()
            .eq(AssetTransfer::getToDepartmentId, departmentId));
        return null;
    }

    private boolean canAccessTransfer(AssetTransfer transfer) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return true;
        }
        return dataPermissionService.canAccessDepartment(transfer.getFromDepartmentId())
            || dataPermissionService.canAccessDepartment(transfer.getToDepartmentId());
    }
}
