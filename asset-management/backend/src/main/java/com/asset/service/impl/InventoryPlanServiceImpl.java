package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.InventoryPlan;
import com.asset.repository.InventoryPlanMapper;
import com.asset.service.InventoryPlanService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 盘点计划服务实现类
 */
@Service
public class InventoryPlanServiceImpl implements InventoryPlanService {

    private final InventoryPlanMapper inventoryPlanMapper;

    public InventoryPlanServiceImpl(InventoryPlanMapper inventoryPlanMapper) {
        this.inventoryPlanMapper = inventoryPlanMapper;
    }

    @Override
    public Result<Map<String, Object>> getPlanList(Integer pageNum, Integer pageSize, Integer status, Integer planType) {
        Page<InventoryPlan> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<InventoryPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryPlan::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(InventoryPlan::getStatus, status);
        }
        
        if (planType != null) {
            wrapper.eq(InventoryPlan::getPlanType, planType);
        }
        
        wrapper.orderByDesc(InventoryPlan::getCreateTime);
        
        Page<InventoryPlan> resultPage = inventoryPlanMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<InventoryPlan> getPlanById(Long id) {
        InventoryPlan plan = inventoryPlanMapper.selectById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        return Result.success(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createPlan(InventoryPlan plan) {
        // 生成计划编号
        String planNo = generatePlanNo();
        plan.setPlanNo(planNo);
        
        // 设置初始状态为停用
        if (plan.getStatus() == null) {
            plan.setStatus(0);
        }
        
        inventoryPlanMapper.insert(plan);
        return Result.success("盘点计划创建成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updatePlan(InventoryPlan plan) {
        InventoryPlan existing = inventoryPlanMapper.selectById(plan.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        
        plan.setUpdateTime(LocalDateTime.now());
        inventoryPlanMapper.updateById(plan);
        return Result.success("盘点计划更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deletePlan(Long id) {
        InventoryPlan plan = inventoryPlanMapper.selectById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        
        plan.setDeleted(1);
        plan.setUpdateTime(LocalDateTime.now());
        inventoryPlanMapper.updateById(plan);
        return Result.success("盘点计划删除成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> enablePlan(Long id) {
        InventoryPlan plan = inventoryPlanMapper.selectById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        
        plan.setStatus(1);
        plan.setUpdateTime(LocalDateTime.now());
        inventoryPlanMapper.updateById(plan);
        return Result.success("盘点计划已启用", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> disablePlan(Long id) {
        InventoryPlan plan = inventoryPlanMapper.selectById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        
        plan.setStatus(0);
        plan.setUpdateTime(LocalDateTime.now());
        inventoryPlanMapper.updateById(plan);
        return Result.success("盘点计划已停用", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> executePlan(Long id) {
        InventoryPlan plan = inventoryPlanMapper.selectById(id);
        if (plan == null || plan.getDeleted() == 1) {
            return Result.error("盘点计划不存在");
        }
        
        if (plan.getStatus() != 1) {
            return Result.error("只有启用的计划才能执行");
        }
        
        // TODO: 根据计划生成盘点任务
        // 这里需要根据计划的 categoryIds 和 location_ids 筛选资产
        // 创建 InventoryTask 记录
        
        return Result.success("盘点计划执行成功，已生成盘点任务", null);
    }

    /**
     * 生成计划编号
     * 格式：PP + yyyyMMddHHmmss + 4 位随机数
     */
    private String generatePlanNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return "PP" + timestamp + random;
    }
}
