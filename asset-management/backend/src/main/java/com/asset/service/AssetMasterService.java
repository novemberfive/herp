package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.AssetCategory;
import com.asset.entity.AssetMaster;
import com.asset.repository.AssetCategoryMapper;
import com.asset.repository.AssetMasterMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 资产主数据服务类
 */
@Service
public class AssetMasterService {

    private final AssetMasterMapper assetMasterMapper;
    private final AssetCategoryMapper assetCategoryMapper;
    private final OperationLogService operationLogService;

    public AssetMasterService(AssetMasterMapper assetMasterMapper,
                              AssetCategoryMapper assetCategoryMapper,
                              OperationLogService operationLogService) {
        this.assetMasterMapper = assetMasterMapper;
        this.assetCategoryMapper = assetCategoryMapper;
        this.operationLogService = operationLogService;
    }

    @Cacheable(value = "assetMaster:list")
    public Result<List<AssetMaster>> getAssetMasterList() {
        return Result.success(assetMasterMapper.selectAllWithCategory());
    }

    @Cacheable(value = "assetMaster:enabled")
    public Result<List<AssetMaster>> getEnabledAssetMasterList() {
        return Result.success(assetMasterMapper.selectEnabledWithCategory());
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"assetMaster:list", "assetMaster:enabled"}, allEntries = true)
    public Result<Void> createAssetMaster(AssetMaster assetMaster) {
        String validateMessage = validateAssetMaster(assetMaster, false);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(assetMaster);
        assetMasterMapper.insert(assetMaster);
        Result<Void> result = Result.success("资产主数据创建成功", null);
        operationLogService.record("BASIC", "CREATE_ASSET_MASTER", "asset_master", String.valueOf(assetMaster.getId()), "创建资产主数据：" + assetMaster.getAssetCode(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"assetMaster:list", "assetMaster:enabled"}, allEntries = true)
    public Result<Void> updateAssetMaster(AssetMaster assetMaster) {
        if (assetMaster.getId() == null) {
            return Result.error("资产主数据 ID 不能为空");
        }

        AssetMaster existing = assetMasterMapper.selectById(assetMaster.getId());
        if (existing == null || Objects.equals(existing.getDeleted(), 1)) {
            return Result.error("资产主数据不存在");
        }

        String validateMessage = validateAssetMaster(assetMaster, true);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(assetMaster);
        assetMasterMapper.updateById(assetMaster);
        Result<Void> result = Result.success("资产主数据更新成功", null);
        operationLogService.record("BASIC", "UPDATE_ASSET_MASTER", "asset_master", String.valueOf(assetMaster.getId()), "更新资产主数据：" + assetMaster.getAssetCode(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"assetMaster:list", "assetMaster:enabled"}, allEntries = true)
    public Result<Void> updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值无效");
        }

        AssetMaster existing = assetMasterMapper.selectById(id);
        if (existing == null || Objects.equals(existing.getDeleted(), 1)) {
            return Result.error("资产主数据不存在");
        }

        existing.setStatus(status);
        assetMasterMapper.updateById(existing);
        Result<Void> result = Result.success(status == 1 ? "资产主数据已启用" : "资产主数据已停用", null);
        operationLogService.record("BASIC", "UPDATE_ASSET_MASTER_STATUS", "asset_master", String.valueOf(existing.getId()), (status == 1 ? "启用" : "停用") + "资产主数据：" + existing.getAssetCode(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"assetMaster:list", "assetMaster:enabled"}, allEntries = true)
    public Result<Void> deleteAssetMaster(Long id) {
        AssetMaster existing = assetMasterMapper.selectById(id);
        if (existing == null || Objects.equals(existing.getDeleted(), 1)) {
            return Result.error("资产主数据不存在");
        }

        existing.setDeleted(1);
        assetMasterMapper.updateById(existing);
        Result<Void> result = Result.success("资产主数据删除成功", null);
        operationLogService.record("BASIC", "DELETE_ASSET_MASTER", "asset_master", String.valueOf(existing.getId()), "删除资产主数据：" + existing.getAssetCode(), result);
        return result;
    }

    private void fillDefaults(AssetMaster assetMaster) {
        if (assetMaster.getUnit() != null) {
            assetMaster.setUnit(assetMaster.getUnit().trim());
        }
        if (assetMaster.getSpecification() != null) {
            assetMaster.setSpecification(assetMaster.getSpecification().trim());
        }
        if (assetMaster.getBrand() != null) {
            assetMaster.setBrand(assetMaster.getBrand().trim());
        }
        if (assetMaster.getRemark() != null) {
            assetMaster.setRemark(assetMaster.getRemark().trim());
        }
        if (assetMaster.getStatus() == null) {
            assetMaster.setStatus(1);
        }
        if (assetMaster.getStockQuantity() == null) {
            assetMaster.setStockQuantity(0);
        }
        if (assetMaster.getMinStock() == null) {
            assetMaster.setMinStock(0);
        }
    }

    private String validateAssetMaster(AssetMaster assetMaster, boolean updating) {
        if (!StringUtils.hasText(assetMaster.getAssetCode())) {
            return "资产编码不能为空";
        }
        if (!StringUtils.hasText(assetMaster.getAssetName())) {
            return "资产名称不能为空";
        }
        if (assetMaster.getCategoryId() == null) {
            return "资产分类不能为空";
        }
        if (!StringUtils.hasText(assetMaster.getUnit())) {
            return "计量单位不能为空";
        }

        AssetCategory category = assetCategoryMapper.selectById(assetMaster.getCategoryId());
        if (category == null || Objects.equals(category.getDeleted(), 1)) {
            return "资产分类不存在";
        }

        AssetMaster sameCode = assetMasterMapper.selectByAssetCode(assetMaster.getAssetCode().trim());
        if (sameCode != null && (!updating || !sameCode.getId().equals(assetMaster.getId()))) {
            return "资产编码已存在";
        }

        assetMaster.setAssetCode(assetMaster.getAssetCode().trim());
        assetMaster.setAssetName(assetMaster.getAssetName().trim());
        return null;
    }
}
