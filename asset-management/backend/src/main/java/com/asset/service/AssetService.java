package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetCard;
import com.asset.repository.AssetCardMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 资产服务类
 */
@Service
public class AssetService {

    private final AssetCardMapper assetCardMapper;

    public AssetService(AssetCardMapper assetCardMapper) {
        this.assetCardMapper = assetCardMapper;
    }

    /**
     * 分页查询资产列表
     */
    @Cacheable(value = "asset:list", key = "#pageNum + '_' + #pageSize + '_' + #status")
    public Result<Map<String, Object>> getAssetList(Integer pageNum, Integer pageSize, Integer status) {
        Page<AssetCard> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(AssetCard::getStatus, status);
        }
        
        wrapper.orderByDesc(AssetCard::getCreateTime);
        
        Page<AssetCard> resultPage = assetCardMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    /**
     * 获取当前用户的资产列表（我的资产）
     */
    @Cacheable(value = "asset:myassets", key = "#userId + '_' + #pageNum + '_' + #pageSize")
    public Result<Map<String, Object>> getMyAssets(Long userId, Integer pageNum, Integer pageSize) {
        Page<AssetCard> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        wrapper.eq(AssetCard::getUserId, userId);
        wrapper.orderByDesc(AssetCard::getCreateTime);
        
        Page<AssetCard> resultPage = assetCardMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    /**
     * 获取部门资产列表
     */
    @Cacheable(value = "asset:deptassets", key = "#departmentId + '_' + #pageNum + '_' + #pageSize")
    public Result<Map<String, Object>> getDeptAssets(Long departmentId, Integer pageNum, Integer pageSize) {
        Page<AssetCard> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        wrapper.eq(AssetCard::getDepartmentId, departmentId);
        wrapper.orderByDesc(AssetCard::getCreateTime);
        
        Page<AssetCard> resultPage = assetCardMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    /**
     * 根据 ID 查询资产详情
     */
    @Cacheable(value = "asset:detail", key = "#id")
    public Result<AssetCard> getAssetById(Long id) {
        AssetCard asset = assetCardMapper.selectById(id);
        if (asset == null || asset.getDeleted() == 1) {
            return Result.error("资产不存在");
        }
        return Result.success(asset);
    }

    /**
     * 创建资产
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "asset:list", allEntries = true)
    public Result<Void> createAsset(AssetCard asset) {
        // 生成资产编码
        String assetCode = generateAssetCode();
        asset.setAssetCode(assetCode);
        
        // 设置初始状态为闲置
        if (asset.getStatus() == null) {
            asset.setStatus(0);
        }
        
        // 计算总金额
        if (asset.getUnitPrice() != null && asset.getQuantity() != null) {
            asset.setTotalAmount(asset.getUnitPrice().multiply(java.math.BigDecimal.valueOf(asset.getQuantity())));
        }
        
        assetCardMapper.insert(asset);
        return Result.success("资产创建成功", null);
    }

    /**
     * 更新资产
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"asset:list", "asset:detail"}, allEntries = true)
    public Result<Void> updateAsset(AssetCard asset) {
        AssetCard existing = assetCardMapper.selectById(asset.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("资产不存在");
        }
        
        // 重新计算总金额
        if (asset.getUnitPrice() != null && asset.getQuantity() != null) {
            asset.setTotalAmount(asset.getUnitPrice().multiply(java.math.BigDecimal.valueOf(asset.getQuantity())));
        }
        
        assetCardMapper.updateById(asset);
        return Result.success("资产更新成功", null);
    }

    /**
     * 删除资产（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"asset:list", "asset:detail"}, allEntries = true)
    public Result<Void> deleteAsset(Long id) {
        AssetCard asset = assetCardMapper.selectById(id);
        if (asset == null || asset.getDeleted() == 1) {
            return Result.error("资产不存在");
        }
        
        asset.setDeleted(1);
        assetCardMapper.updateById(asset);
        return Result.success("资产删除成功", null);
    }

    /**
     * 获取资产统计信息
     */
    @Cacheable(value = "asset:stats")
    public Result<Map<String, Object>> getStats() {
        List<AssetCardMapper.StatusCount> statusCounts = assetCardMapper.countByStatus();
        
        long total = assetCardMapper.selectCount(new LambdaQueryWrapper<AssetCard>().eq(AssetCard::getDeleted, 0));
        
        Map<String, Object> data = Map.of(
            "total", total,
            "statusCounts", statusCounts
        );
        
        return Result.success(data);
    }

    /**
     * 生成资产编码
     * 规则：AS + 年月日 + 4 位序号
     */
    private synchronized String generateAssetCode() {
        String datePrefix = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        wrapper.like(AssetCard::getAssetCode, "AS" + datePrefix);
        wrapper.orderByDesc(AssetCard::getAssetCode);
        
        AssetCard lastAsset = assetCardMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastAsset != null && lastAsset.getAssetCode().length() > 12) {
            try {
                sequence = Integer.parseInt(lastAsset.getAssetCode().substring(12)) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return "AS" + datePrefix + String.format("%04d", sequence);
    }
}
