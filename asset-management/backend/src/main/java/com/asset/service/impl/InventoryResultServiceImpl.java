package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.InventoryResult;
import com.asset.repository.InventoryResultMapper;
import com.asset.service.InventoryResultService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 盘点结果服务实现类
 */
@Service
public class InventoryResultServiceImpl implements InventoryResultService {

    private final InventoryResultMapper inventoryResultMapper;

    public InventoryResultServiceImpl(InventoryResultMapper inventoryResultMapper) {
        this.inventoryResultMapper = inventoryResultMapper;
    }

    @Override
    public Result<Map<String, Object>> getResultList(Integer pageNum, Integer pageSize, Long taskId, Integer resultType, Integer reviewStatus) {
        Page<InventoryResult> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<InventoryResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryResult::getDeleted, 0);
        
        if (taskId != null) {
            wrapper.eq(InventoryResult::getTaskId, taskId);
        }
        
        if (resultType != null) {
            wrapper.eq(InventoryResult::getResultType, resultType);
        }
        
        if (reviewStatus != null) {
            wrapper.eq(InventoryResult::getReviewStatus, reviewStatus);
        }
        
        wrapper.orderByDesc(InventoryResult::getCreateTime);
        
        Page<InventoryResult> resultPage = inventoryResultMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<InventoryResult> getResultById(Long id) {
        InventoryResult result = inventoryResultMapper.selectById(id);
        if (result == null || result.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createResult(InventoryResult result) {
        // 计算差异数量
        if (result.getBookQuantity() != null && result.getActualQuantity() != null) {
            result.setDifferenceQuantity(result.getActualQuantity() - result.getBookQuantity());
        }
        
        // 根据差异自动判断结果类型
        if (result.getDifferenceQuantity() != null) {
            if (result.getDifferenceQuantity() > 0) {
                result.setResultType(1); // 盘盈
            } else if (result.getDifferenceQuantity() < 0) {
                result.setResultType(2); // 盘亏
            } else {
                result.setResultType(0); // 正常
            }
        }
        
        // 设置初始复核状态为待复核
        if (result.getReviewStatus() == null) {
            result.setReviewStatus(0);
        }
        
        // 设置初始处理状态为待处理
        if (result.getProcessStatus() == null) {
            result.setProcessStatus(0);
        }
        
        inventoryResultMapper.insert(result);
        return Result.success("盘点结果创建成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateResult(InventoryResult result) {
        InventoryResult existing = inventoryResultMapper.selectById(result.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        
        // 已处理的結果不能修改
        if (existing.getProcessStatus() == 1) {
            return Result.error("已处理的盘点结果不能修改");
        }
        
        // 重新计算差异
        if (result.getBookQuantity() != null && result.getActualQuantity() != null) {
            result.setDifferenceQuantity(result.getActualQuantity() - result.getBookQuantity());
        }
        
        result.setUpdateTime(LocalDateTime.now());
        inventoryResultMapper.updateById(result);
        return Result.success("盘点结果更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteResult(Long id) {
        InventoryResult result = inventoryResultMapper.selectById(id);
        if (result == null || result.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        
        result.setDeleted(1);
        result.setUpdateTime(LocalDateTime.now());
        inventoryResultMapper.updateById(result);
        return Result.success("盘点结果删除成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> submitResult(Long id, Long inventoryUserId, String inventoryUserName) {
        InventoryResult result = inventoryResultMapper.selectById(id);
        if (result == null || result.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        
        result.setInventoryUserId(inventoryUserId);
        result.setInventoryUserName(inventoryUserName);
        result.setInventoryTime(LocalDateTime.now());
        result.setUpdateTime(LocalDateTime.now());
        
        inventoryResultMapper.updateById(result);
        return Result.success("盘点结果已提交", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> reviewResult(Long id, Long reviewerId, String reviewerName, Integer reviewStatus, String reviewRemark) {
        InventoryResult result = inventoryResultMapper.selectById(id);
        if (result == null || result.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        
        result.setReviewerId(reviewerId);
        result.setReviewerName(reviewerName);
        result.setReviewTime(LocalDateTime.now());
        result.setReviewStatus(reviewStatus);
        result.setReviewRemark(reviewRemark);
        result.setUpdateTime(LocalDateTime.now());
        
        // 如果复核不通过，重置处理状态
        if (reviewStatus == 2) {
            result.setProcessStatus(0);
        }
        
        inventoryResultMapper.updateById(result);
        return Result.success("盘点结果复核完成", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> processResult(Long id, Long processUserId, String processUserName, String processRemark) {
        InventoryResult result = inventoryResultMapper.selectById(id);
        if (result == null || result.getDeleted() == 1) {
            return Result.error("盘点结果不存在");
        }
        
        // 只有复核通过的才能处理
        if (result.getReviewStatus() != 1) {
            return Result.error("只有复核通过的盘点结果才能处理");
        }
        
        result.setProcessUserId(processUserId);
        result.setProcessUserName(processUserName);
        result.setProcessTime(LocalDateTime.now());
        result.setProcessStatus(1);
        result.setProcessRemark(processRemark);
        result.setUpdateTime(LocalDateTime.now());
        
        inventoryResultMapper.updateById(result);
        return Result.success("盘点结果已处理", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchImportResults(Long taskId, List<InventoryResult> results) {
        if (results == null || results.isEmpty()) {
            return Result.error("导入数据为空");
        }
        
        for (InventoryResult result : results) {
            result.setTaskId(taskId);
            
            // 计算差异数量
            if (result.getBookQuantity() != null && result.getActualQuantity() != null) {
                result.setDifferenceQuantity(result.getActualQuantity() - result.getBookQuantity());
            }
            
            // 根据差异自动判断结果类型
            if (result.getDifferenceQuantity() != null) {
                if (result.getDifferenceQuantity() > 0) {
                    result.setResultType(1); // 盘盈
                } else if (result.getDifferenceQuantity() < 0) {
                    result.setResultType(2); // 盘亏
                } else {
                    result.setResultType(0); // 正常
                }
            }
            
            result.setReviewStatus(0);
            result.setProcessStatus(0);
            
            inventoryResultMapper.insert(result);
        }
        
        return Result.success("批量导入成功，共导入" + results.size() + "条记录", null);
    }
}
