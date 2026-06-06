package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.AssetBorrow;
import com.asset.repository.AssetBorrowMapper;
import com.asset.service.AssetBorrowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 资产借用服务实现类
 */
@Service
public class AssetBorrowServiceImpl implements AssetBorrowService {

    private final AssetBorrowMapper assetBorrowMapper;

    public AssetBorrowServiceImpl(AssetBorrowMapper assetBorrowMapper) {
        this.assetBorrowMapper = assetBorrowMapper;
    }

    @Override
    public Result<Map<String, Object>> getBorrowList(Integer pageNum, Integer pageSize, Integer borrowStatus,
                                                     Long borrowerId, String borrowNo, String assetCode,
                                                     String assetName, String borrowerName) {
        Page<AssetBorrow> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetBorrow::getDeleted, 0);
        
        if (borrowStatus != null) {
            wrapper.eq(AssetBorrow::getBorrowStatus, borrowStatus);
        }
        
        if (borrowerId != null) {
            wrapper.eq(AssetBorrow::getBorrowerId, borrowerId);
        }
        if (borrowNo != null && !borrowNo.isEmpty()) {
            wrapper.like(AssetBorrow::getBorrowNo, borrowNo);
        }
        if (assetCode != null && !assetCode.isEmpty()) {
            wrapper.like(AssetBorrow::getAssetCode, assetCode);
        }
        if (assetName != null && !assetName.isEmpty()) {
            wrapper.like(AssetBorrow::getAssetName, assetName);
        }
        if (borrowerName != null && !borrowerName.isEmpty()) {
            wrapper.like(AssetBorrow::getBorrowerName, borrowerName);
        }
        
        wrapper.orderByDesc(AssetBorrow::getBorrowDate);
        
        Page<AssetBorrow> resultPage = assetBorrowMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<AssetBorrow> getBorrowById(Long id) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        return Result.success(borrow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createBorrow(AssetBorrow borrow) {
        // 生成借用单号
        String borrowNo = generateBorrowNo();
        borrow.setBorrowNo(borrowNo);
        
        // 设置初始审批状态为待审批
        borrow.setApproveStatus(0);
        
        // 设置初始借用状态为待审批
        borrow.setBorrowStatus(0);
        
        // 设置创建时间
        borrow.setCreateTime(LocalDateTime.now());
        
        assetBorrowMapper.insert(borrow);
        return Result.success("资产借用申请创建成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateBorrow(AssetBorrow borrow) {
        AssetBorrow existing = assetBorrowMapper.selectById(borrow.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        // 已审批的借用申请不能修改
        if (existing.getApproveStatus() != 0) {
            return Result.error("已审批的借用申请不能修改");
        }
        
        borrow.setUpdateTime(LocalDateTime.now());
        assetBorrowMapper.updateById(borrow);
        return Result.success("资产借用申请更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteBorrow(Long id) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        borrow.setDeleted(1);
        borrow.setUpdateTime(LocalDateTime.now());
        assetBorrowMapper.updateById(borrow);
        return Result.success("资产借用记录删除成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveBorrow(Long id, Long approverId, String approverName, Integer approveStatus, String approveRemark) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        if (borrow.getApproveStatus() != 0) {
            return Result.error("该申请已审批过，不能重复审批");
        }
        
        borrow.setApproverId(approverId);
        borrow.setApproverName(approverName);
        borrow.setApproveTime(LocalDateTime.now());
        borrow.setApproveStatus(approveStatus);
        borrow.setApproveRemark(approveRemark);
        borrow.setUpdateTime(LocalDateTime.now());
        
        // 如果审批通过，设置借用状态为借用中
        if (approveStatus == 1) {
            borrow.setBorrowStatus(1);
        } else if (approveStatus == 2) {
            // 如果审批不通过，设置借用状态为已取消
            borrow.setBorrowStatus(4);
        }
        
        assetBorrowMapper.updateById(borrow);
        return Result.success("资产借用申请审批完成", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> returnAsset(Long id, String returnCondition, String returnRemark) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        // 只有借用中的才能归还
        if (borrow.getBorrowStatus() != 1) {
            return Result.error("只有借用中的资产才能归还");
        }
        
        borrow.setActualReturnDate(LocalDate.now());
        borrow.setReturnCondition(returnCondition);
        borrow.setReturnRemark(returnRemark);
        borrow.setBorrowStatus(2); // 已归还
        borrow.setUpdateTime(LocalDateTime.now());
        
        assetBorrowMapper.updateById(borrow);
        return Result.success("资产已归还", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelBorrow(Long id, String reason) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        // 已归还的借用不能取消
        if (borrow.getBorrowStatus() == 2) {
            return Result.error("已归还的借用不能取消");
        }
        
        borrow.setBorrowStatus(4); // 已取消
        borrow.setRemark(reason);
        borrow.setUpdateTime(LocalDateTime.now());
        
        assetBorrowMapper.updateById(borrow);
        return Result.success("资产借用已取消", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> sendOverdueReminder(Long id) {
        AssetBorrow borrow = assetBorrowMapper.selectById(id);
        if (borrow == null || borrow.getDeleted() == 1) {
            return Result.error("资产借用记录不存在");
        }
        
        // 只有借用中的才能发送提醒
        if (borrow.getBorrowStatus() != 1) {
            return Result.error("只有借用中的资产才能发送逾期提醒");
        }
        
        // 检查是否已逾期
        if (borrow.getExpectedReturnDate() != null && 
            borrow.getExpectedReturnDate().isBefore(LocalDate.now())) {
            
            borrow.setReminderSent(1);
            borrow.setLastReminderTime(LocalDateTime.now());
            borrow.setUpdateTime(LocalDateTime.now());
            
            assetBorrowMapper.updateById(borrow);
            
            // TODO: 这里可以集成消息通知系统发送提醒
            // 例如：发送邮件、短信或站内消息
            
            return Result.success("逾期提醒已发送", null);
        } else {
            return Result.error("该借用尚未逾期");
        }
    }

    /**
     * 生成借用单号
     * 格式：BR + yyyyMMddHHmmss + 4 位随机数
     */
    private String generateBorrowNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return "BR" + timestamp + random;
    }
}
