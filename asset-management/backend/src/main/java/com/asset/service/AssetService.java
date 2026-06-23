package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetCard;
import com.asset.entity.SysUser;
import com.asset.repository.AssetCardMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cache.annotation.CacheEvict;
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
    private final DataPermissionService dataPermissionService;
    private final OperationLogService operationLogService;

    public AssetService(AssetCardMapper assetCardMapper,
                        DataPermissionService dataPermissionService,
                        OperationLogService operationLogService) {
        this.assetCardMapper = assetCardMapper;
        this.dataPermissionService = dataPermissionService;
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询资产列表
     */
    public Result<Map<String, Object>> getAssetList(Integer pageNum, Integer pageSize, Integer status) {
        Page<AssetCard> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);

        if (status != null) {
            wrapper.eq(AssetCard::getStatus, status);
        }

        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(null);
        SysUser currentUser = dataPermissionService.getCurrentUser();
        if (currentUser != null && !dataPermissionService.isAdmin(currentUser)) {
            if (scopedDepartmentId == null) {
                return Result.forbidden("当前用户未绑定部门，无法查看资产列表");
            }
            wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
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
    public Result<Map<String, Object>> getDeptAssets(Long departmentId, Integer pageNum, Integer pageSize) {
        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(departmentId);
        if (dataPermissionService.shouldRestrictToOwnDepartment() && scopedDepartmentId == null) {
            return Result.forbidden("当前用户未绑定部门，无法查看部门资产");
        }

        Page<AssetCard> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
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
    public Result<AssetCard> getAssetById(Long id) {
        AssetCard asset = assetCardMapper.selectById(id);
        if (asset == null || asset.getDeleted() == 1) {
            return Result.error("资产不存在");
        }
        if (!dataPermissionService.canAccessAsset(asset)) {
            return Result.forbidden("无权查看该资产");
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
        Result<Void> result = Result.success("资产创建成功", null);
        operationLogService.record("ASSET", "CREATE_ASSET", "asset_card", String.valueOf(asset.getId()), "创建资产：" + asset.getAssetCode(), result);
        return result;
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
        Result<Void> result = Result.success("资产更新成功", null);
        operationLogService.record("ASSET", "UPDATE_ASSET", "asset_card", String.valueOf(asset.getId()), "更新资产：" + existing.getAssetCode(), result);
        return result;
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
        Result<Void> result = Result.success("资产删除成功", null);
        operationLogService.record("ASSET", "DELETE_ASSET", "asset_card", String.valueOf(asset.getId()), "删除资产：" + asset.getAssetCode(), result);
        return result;
    }

    /**
     * 获取资产统计信息
     */
    public Result<Map<String, Object>> getStats() {
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);

        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(null);
        SysUser currentUser = dataPermissionService.getCurrentUser();
        if (currentUser != null && !dataPermissionService.isAdmin(currentUser)) {
            if (scopedDepartmentId == null) {
                return Result.forbidden("当前用户未绑定部门，无法查看统计信息");
            }
            wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
        }

        List<AssetCard> assets = assetCardMapper.selectList(wrapper);
        Map<Integer, Long> statusCountMap = assets.stream()
                .collect(java.util.stream.Collectors.groupingBy(AssetCard::getStatus, java.util.stream.Collectors.counting()));
        List<AssetCardMapper.StatusCount> statusCounts = statusCountMap.entrySet().stream()
                .map(entry -> {
                    AssetCardMapper.StatusCount item = new AssetCardMapper.StatusCount();
                    item.setStatus(entry.getKey());
                    item.setCount(entry.getValue());
                    return item;
                })
                .toList();

        long total = assets.size();

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
