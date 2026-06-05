package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.AssetDisposal;
import com.asset.repository.AssetDisposalMapper;
import com.asset.service.AssetDisposalService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 资产处置服务实现类
 */
@Service
public class AssetDisposalServiceImpl implements AssetDisposalService {

    private final AssetDisposalMapper assetDisposalMapper;

    public AssetDisposalServiceImpl(AssetDisposalMapper assetDisposalMapper) {
        this.assetDisposalMapper = assetDisposalMapper;
    }

    @Override
    public Result<Map<String, Object>> getDisposalList(Integer pageNum, Integer pageSize, Integer disposalType,
                                                       Integer approveStatus, Integer disposalStatus,
                                                       String disposalNo, String assetCode, String assetName) {
        Page<AssetDisposal> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetDisposal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetDisposal::getDeleted, 0);
        
        if (disposalType != null) {
            wrapper.eq(AssetDisposal::getDisposalType, disposalType);
        }
        
        if (approveStatus != null) {
            wrapper.eq(AssetDisposal::getApproveStatus, approveStatus);
        }
        
        if (disposalStatus != null) {
            wrapper.eq(AssetDisposal::getDisposalStatus, disposalStatus);
        }
        if (disposalNo != null && !disposalNo.isEmpty()) {
            wrapper.like(AssetDisposal::getDisposalNo, disposalNo);
        }
        if (assetCode != null && !assetCode.isEmpty()) {
            wrapper.like(AssetDisposal::getAssetCode, assetCode);
        }
        if (assetName != null && !assetName.isEmpty()) {
            wrapper.like(AssetDisposal::getAssetName, assetName);
        }
        
        wrapper.orderByDesc(AssetDisposal::getApplyTime);
        
        Page<AssetDisposal> resultPage = assetDisposalMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<AssetDisposal> getDisposalById(Long id) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        return Result.success(disposal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createDisposal(AssetDisposal disposal) {
        // 生成处置单号
        String disposalNo = generateDisposalNo();
        disposal.setDisposalNo(disposalNo);
        
        // 设置初始审批状态为待审批
        disposal.setApproveStatus(0);
        
        // 设置初始处置状态为待处置
        disposal.setDisposalStatus(0);
        
        // 设置申请时间
        disposal.setApplyTime(LocalDateTime.now());
        
        assetDisposalMapper.insert(disposal);
        return Result.success("资产处置申请创建成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDisposal(AssetDisposal disposal) {
        AssetDisposal existing = assetDisposalMapper.selectById(disposal.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        // 已审批的处置申请不能修改
        if (existing.getApproveStatus() != 0) {
            return Result.error("已审批的处置申请不能修改");
        }
        
        disposal.setUpdateTime(LocalDateTime.now());
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置申请更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteDisposal(Long id) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        disposal.setDeleted(1);
        disposal.setUpdateTime(LocalDateTime.now());
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置申请删除成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveDisposal(Long id, Long approverId, String approverName, Integer approveStatus, String approveRemark) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        if (disposal.getApproveStatus() != 0) {
            return Result.error("该申请已审批过，不能重复审批");
        }
        
        disposal.setApproverId(approverId);
        disposal.setApproverName(approverName);
        disposal.setApproveTime(LocalDateTime.now());
        disposal.setApproveStatus(approveStatus);
        disposal.setApproveRemark(approveRemark);
        disposal.setUpdateTime(LocalDateTime.now());
        
        // 如果审批不通过，设置处置状态为已取消
        if (approveStatus == 2) {
            disposal.setDisposalStatus(3);
        }
        
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置申请审批完成", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> executeDisposal(Long id, Integer disposalMethod, BigDecimal actualValue, String buyerName, String buyerContact) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        // 只有审批通过的才能执行
        if (disposal.getApproveStatus() != 1) {
            return Result.error("只有审批通过的处置申请才能执行");
        }
        
        if (disposal.getDisposalStatus() != 0) {
            return Result.error("该处置申请已开始执行或已完成");
        }
        
        disposal.setDisposalMethod(disposalMethod);
        disposal.setActualValue(actualValue);
        disposal.setBuyerName(buyerName);
        disposal.setBuyerContact(buyerContact);
        disposal.setDisposalStatus(1); // 处置中
        disposal.setUpdateTime(LocalDateTime.now());
        
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置开始执行", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeDisposal(Long id) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        if (disposal.getDisposalStatus() != 1) {
            return Result.error("只有处置中的申请才能完成");
        }
        
        disposal.setDisposalStatus(2); // 已完成
        disposal.setCompleteTime(LocalDateTime.now());
        disposal.setUpdateTime(LocalDateTime.now());
        
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置已完成", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelDisposal(Long id, String reason) {
        AssetDisposal disposal = assetDisposalMapper.selectById(id);
        if (disposal == null || disposal.getDeleted() == 1) {
            return Result.error("资产处置申请不存在");
        }
        
        // 已完成的处置不能取消
        if (disposal.getDisposalStatus() == 2) {
            return Result.error("已完成的处置不能取消");
        }
        
        disposal.setDisposalStatus(3); // 已取消
        disposal.setRemark(reason);
        disposal.setUpdateTime(LocalDateTime.now());
        
        assetDisposalMapper.updateById(disposal);
        return Result.success("资产处置已取消", null);
    }

    /**
     * 生成处置单号
     * 格式：DP + yyyyMMddHHmmss + 4 位随机数
     */
    private String generateDisposalNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return "DP" + timestamp + random;
    }
}
