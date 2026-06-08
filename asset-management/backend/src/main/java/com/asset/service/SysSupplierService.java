package com.asset.service;

import com.asset.dto.Result;
import com.asset.entity.SysSupplier;
import com.asset.repository.SysSupplierMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统供应商服务类
 */
@Service
public class SysSupplierService {

    private final SysSupplierMapper sysSupplierMapper;
    private final OperationLogService operationLogService;

    public SysSupplierService(SysSupplierMapper sysSupplierMapper, OperationLogService operationLogService) {
        this.sysSupplierMapper = sysSupplierMapper;
        this.operationLogService = operationLogService;
    }

    @Cacheable(value = "supplier:list")
    public Result<List<SysSupplier>> getSupplierList() {
        return Result.success(sysSupplierMapper.selectAll());
    }

    @Cacheable(value = "supplier:enabled")
    public Result<List<SysSupplier>> getEnabledSupplierList() {
        return Result.success(sysSupplierMapper.selectEnabled());
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"supplier:list", "supplier:enabled"}, allEntries = true)
    public Result<Void> createSupplier(SysSupplier supplier) {
        String validateMessage = validateSupplier(supplier, false);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(supplier);
        sysSupplierMapper.insert(supplier);
        Result<Void> result = Result.success("供应商创建成功", null);
        operationLogService.record("BASIC", "CREATE_SUPPLIER", "sys_supplier", String.valueOf(supplier.getId()), "创建供应商：" + supplier.getSupplierName(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"supplier:list", "supplier:enabled"}, allEntries = true)
    public Result<Void> updateSupplier(SysSupplier supplier) {
        if (supplier.getId() == null) {
            return Result.error("供应商 ID 不能为空");
        }

        SysSupplier existing = sysSupplierMapper.selectById(supplier.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("供应商不存在");
        }

        String validateMessage = validateSupplier(supplier, true);
        if (validateMessage != null) {
            return Result.error(validateMessage);
        }

        fillDefaults(supplier);
        sysSupplierMapper.updateById(supplier);
        Result<Void> result = Result.success("供应商更新成功", null);
        operationLogService.record("BASIC", "UPDATE_SUPPLIER", "sys_supplier", String.valueOf(supplier.getId()), "更新供应商：" + supplier.getSupplierName(), result);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"supplier:list", "supplier:enabled"}, allEntries = true)
    public Result<Void> deleteSupplier(Long id) {
        SysSupplier existing = sysSupplierMapper.selectById(id);
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("供应商不存在");
        }

        existing.setDeleted(1);
        sysSupplierMapper.updateById(existing);
        Result<Void> result = Result.success("供应商删除成功", null);
        operationLogService.record("BASIC", "DELETE_SUPPLIER", "sys_supplier", String.valueOf(existing.getId()), "删除供应商：" + existing.getSupplierName(), result);
        return result;
    }

    private void fillDefaults(SysSupplier supplier) {
        if (supplier.getStatus() == null) {
            supplier.setStatus(1);
        }
        if (supplier.getSortOrder() == null) {
            supplier.setSortOrder(0);
        }
    }

    private String validateSupplier(SysSupplier supplier, boolean updating) {
        if (!StringUtils.hasText(supplier.getSupplierName())) {
            return "供应商名称不能为空";
        }
        if (!StringUtils.hasText(supplier.getSupplierCode())) {
            return "供应商编码不能为空";
        }

        SysSupplier sameName = sysSupplierMapper.selectBySupplierName(supplier.getSupplierName().trim());
        if (sameName != null && (!updating || !sameName.getId().equals(supplier.getId()))) {
            return "供应商名称已存在";
        }

        SysSupplier sameCode = sysSupplierMapper.selectBySupplierCode(supplier.getSupplierCode().trim());
        if (sameCode != null && (!updating || !sameCode.getId().equals(supplier.getId()))) {
            return "供应商编码已存在";
        }

        supplier.setSupplierName(supplier.getSupplierName().trim());
        supplier.setSupplierCode(supplier.getSupplierCode().trim());
        return null;
    }
}
