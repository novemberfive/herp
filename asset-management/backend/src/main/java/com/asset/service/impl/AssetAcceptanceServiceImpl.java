package com.asset.service.impl;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetAcceptance;
import com.asset.repository.AssetAcceptanceRepository;
import com.asset.service.AssetAcceptanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 验收登记服务实现类
 */
@Service
public class AssetAcceptanceServiceImpl implements AssetAcceptanceService {
    
    private final AssetAcceptanceRepository assetAcceptanceRepository;
    
    public AssetAcceptanceServiceImpl(AssetAcceptanceRepository assetAcceptanceRepository) {
        this.assetAcceptanceRepository = assetAcceptanceRepository;
    }
    
    @Override
    public Result<PageResult<AssetAcceptance>> getPageList(Integer pageNum, Integer pageSize, AssetAcceptance query) {
        Page<AssetAcceptance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetAcceptance> wrapper = new LambdaQueryWrapper<>();
        
        if (query != null) {
            if (query.getAcceptanceNo() != null && !query.getAcceptanceNo().isEmpty()) {
                wrapper.like(AssetAcceptance::getAcceptanceNo, query.getAcceptanceNo());
            }
            if (query.getPurchaseRequestNo() != null && !query.getPurchaseRequestNo().isEmpty()) {
                wrapper.like(AssetAcceptance::getPurchaseRequestNo, query.getPurchaseRequestNo());
            }
            if (query.getAssetName() != null && !query.getAssetName().isEmpty()) {
                wrapper.like(AssetAcceptance::getAssetName, query.getAssetName());
            }
            if (query.getAcceptanceResult() != null) {
                wrapper.eq(AssetAcceptance::getAcceptanceResult, query.getAcceptanceResult());
            }
            if (query.getStorageStatus() != null) {
                wrapper.eq(AssetAcceptance::getStorageStatus, query.getStorageStatus());
            }
        }
        
        wrapper.eq(AssetAcceptance::getDeleted, 0);
        wrapper.orderByDesc(AssetAcceptance::getCreateTime);
        
        Page<AssetAcceptance> resultPage = assetAcceptanceRepository.selectPage(page, wrapper);
        PageResult<AssetAcceptance> pageResult = new PageResult<>(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getSize(),
            resultPage.getCurrent()
        );
        
        return Result.success(pageResult);
    }
    
    @Override
    public Result<AssetAcceptance> getById(Long id) {
        AssetAcceptance acceptance = assetAcceptanceRepository.selectById(id);
        if (acceptance == null || acceptance.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        return Result.success(acceptance);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(AssetAcceptance acceptance) {
        acceptance.setAcceptanceNo(generateAcceptanceNo());
        acceptance.setAcceptanceDate(LocalDateTime.now());
        if (acceptance.getAcceptanceResult() == null) {
            acceptance.setAcceptanceResult(0);
        }
        if (acceptance.getStorageStatus() == null) {
            acceptance.setStorageStatus(0);
        }
        assetAcceptanceRepository.insert(acceptance);
        return Result.success("验收登记创建成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(AssetAcceptance acceptance) {
        AssetAcceptance existing = assetAcceptanceRepository.selectById(acceptance.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        if (existing.getAcceptanceResult() != 0) {
            return Result.error("已验收的登记不能修改");
        }
        acceptance.setUpdateTime(LocalDateTime.now());
        assetAcceptanceRepository.updateById(acceptance);
        return Result.success("验收登记更新成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(Long id) {
        AssetAcceptance acceptance = assetAcceptanceRepository.selectById(id);
        if (acceptance == null || acceptance.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        acceptance.setDeleted(1);
        assetAcceptanceRepository.updateById(acceptance);
        return Result.success("验收登记删除成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> submit(Long id) {
        AssetAcceptance acceptance = assetAcceptanceRepository.selectById(id);
        if (acceptance == null || acceptance.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        if (acceptance.getAcceptanceResult() != 0) {
            return Result.error("只有待验收状态的登记可以提交");
        }
        acceptance.setUpdateTime(LocalDateTime.now());
        assetAcceptanceRepository.updateById(acceptance);
        return Result.success("验收登记已提交", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> accept(Long id, String opinion) {
        AssetAcceptance acceptance = assetAcceptanceRepository.selectById(id);
        if (acceptance == null || acceptance.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        if (acceptance.getAcceptanceResult() != 0) {
            return Result.error("只有待验收状态的登记可以验收");
        }
        
        // 计算合格和不合格数量
        if (acceptance.getQualifiedQuantity() == null) {
            acceptance.setQualifiedQuantity(acceptance.getQuantity());
        }
        if (acceptance.getUnqualifiedQuantity() == null) {
            acceptance.setUnqualifiedQuantity(0);
        }
        
        // 设置验收结果
        if (acceptance.getUnqualifiedQuantity() == 0) {
            acceptance.setAcceptanceResult(1); // 全部合格
        } else if (acceptance.getQualifiedQuantity() == 0) {
            acceptance.setAcceptanceResult(2); // 全部不合格
        } else {
            acceptance.setAcceptanceResult(3); // 部分合格
        }
        
        acceptance.setHandlingOpinion(opinion);
        acceptance.setUpdateTime(LocalDateTime.now());
        assetAcceptanceRepository.updateById(acceptance);
        return Result.success("验收通过", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> reject(Long id, String reason) {
        AssetAcceptance acceptance = assetAcceptanceRepository.selectById(id);
        if (acceptance == null || acceptance.getDeleted() == 1) {
            return Result.error("验收登记不存在");
        }
        if (acceptance.getAcceptanceResult() != 0) {
            return Result.error("只有待验收状态的登记可以验收");
        }
        
        acceptance.setQualifiedQuantity(0);
        acceptance.setUnqualifiedQuantity(acceptance.getQuantity());
        acceptance.setAcceptanceResult(2); // 不合格
        acceptance.setUnqualifiedReason(reason);
        acceptance.setHandlingOpinion("验收拒绝：" + reason);
        acceptance.setUpdateTime(LocalDateTime.now());
        assetAcceptanceRepository.updateById(acceptance);
        return Result.success("验收拒绝", null);
    }
    
    private String generateAcceptanceNo() {
        return "AC" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
