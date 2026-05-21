package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.AssetMaintenance;
import com.asset.repository.AssetMaintenanceMapper;
import com.asset.service.AssetMaintenanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 资产维修服务实现类
 */
@Service
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private final AssetMaintenanceMapper assetMaintenanceMapper;

    public AssetMaintenanceServiceImpl(AssetMaintenanceMapper assetMaintenanceMapper) {
        this.assetMaintenanceMapper = assetMaintenanceMapper;
    }

    @Override
    public Result<Map<String, Object>> getMaintenanceList(Integer pageNum, Integer pageSize, Integer maintenanceResult, Integer approveStatus) {
        Page<AssetMaintenance> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetMaintenance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetMaintenance::getDeleted, 0);
        
        if (maintenanceResult != null) {
            wrapper.eq(AssetMaintenance::getMaintenanceResult, maintenanceResult);
        }
        
        if (approveStatus != null) {
            wrapper.eq(AssetMaintenance::getApproveStatus, approveStatus);
        }
        
        wrapper.orderByDesc(AssetMaintenance::getCreateTime);
        
        Page<AssetMaintenance> resultPage = assetMaintenanceMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<AssetMaintenance> getMaintenanceById(Long id) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        return Result.success(maintenance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createMaintenance(AssetMaintenance maintenance) {
        // 生成维修单号
        String maintenanceNo = generateMaintenanceNo();
        maintenance.setMaintenanceNo(maintenanceNo);
        
        // 设置初始状态
        maintenance.setApplyTime(LocalDateTime.now());
        maintenance.setApproveStatus(0); // 待审批
        maintenance.setMaintenanceResult(0); // 待维修
        
        assetMaintenanceMapper.insert(maintenance);
        return Result.success("维修申请创建成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateMaintenance(AssetMaintenance maintenance) {
        AssetMaintenance existing = assetMaintenanceMapper.selectById(maintenance.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        maintenance.setUpdateTime(LocalDateTime.now());
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修信息更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteMaintenance(Long id) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        maintenance.setDeleted(1);
        maintenance.setUpdateTime(LocalDateTime.now());
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修记录删除成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approveMaintenance(Long id, Integer approveStatus, String approveRemark, Long approverId) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        if (maintenance.getApproveStatus() != 0) {
            return Result.error("该维修申请已审批过");
        }
        
        maintenance.setApproveStatus(approveStatus);
        maintenance.setApproverId(approverId);
        maintenance.setApproverName(null); // 实际应用中应从用户表获取
        maintenance.setApproveTime(LocalDateTime.now());
        maintenance.setApproveRemark(approveRemark);
        
        if (approveStatus == 2) { // 拒绝
            maintenance.setMaintenanceResult(3); // 无法修复/已取消
        }
        
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修申请审批成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> startMaintenance(Long id, Long maintainerId, String maintainerName) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        if (maintenance.getApproveStatus() != 1) {
            return Result.error("维修申请尚未通过审批");
        }
        
        maintenance.setMaintainerId(maintainerId);
        maintenance.setMaintainerName(maintainerName);
        maintenance.setStartDate(LocalDateTime.now());
        maintenance.setMaintenanceResult(1); // 维修中
        
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修已开始", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeMaintenance(Long id, Integer maintenanceResult, String maintenanceMethod, BigDecimal maintenanceCost) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        maintenance.setMaintenanceResult(maintenanceResult);
        maintenance.setMaintenanceMethod(maintenanceMethod);
        maintenance.setMaintenanceCost(maintenanceCost);
        maintenance.setEndDate(LocalDateTime.now());
        
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修已完成", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelMaintenance(Long id) {
        AssetMaintenance maintenance = assetMaintenanceMapper.selectById(id);
        if (maintenance == null || maintenance.getDeleted() == 1) {
            return Result.error("维修记录不存在");
        }
        
        if (maintenance.getMaintenanceResult() == 2) {
            return Result.error("已完成的维修不能取消");
        }
        
        maintenance.setMaintenanceResult(3); // 无法修复/已取消
        maintenance.setEndDate(LocalDateTime.now());
        
        assetMaintenanceMapper.updateById(maintenance);
        return Result.success("维修已取消", null);
    }

    /**
     * 生成维修单号
     * 格式：WX + yyyyMMddHHmmss + 4 位随机数
     */
    private String generateMaintenanceNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return "WX" + timestamp + random;
    }
}
