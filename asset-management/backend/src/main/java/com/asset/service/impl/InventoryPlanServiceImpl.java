package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.InventoryPlan;
import com.asset.entity.InventoryTask;
import com.asset.repository.InventoryPlanMapper;
import com.asset.repository.InventoryTaskMapper;
import com.asset.service.InventoryPlanService;
import com.asset.service.DataPermissionService;
import com.asset.service.OperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 盘点计划服务实现类
 */
@Service
public class InventoryPlanServiceImpl implements InventoryPlanService {

    private final InventoryPlanMapper inventoryPlanMapper;
    private final InventoryTaskMapper inventoryTaskMapper;
    private final DataPermissionService dataPermissionService;
    private final OperationLogService operationLogService;

    public InventoryPlanServiceImpl(InventoryPlanMapper inventoryPlanMapper,
                                    InventoryTaskMapper inventoryTaskMapper,
                                    DataPermissionService dataPermissionService,
                                    OperationLogService operationLogService) {
        this.inventoryPlanMapper = inventoryPlanMapper;
        this.inventoryTaskMapper = inventoryTaskMapper;
        this.dataPermissionService = dataPermissionService;
        this.operationLogService = operationLogService;
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

        Result<Map<String, Object>> scopeResult = applyCreatorScope(wrapper);
        if (scopeResult != null) {
            return scopeResult;
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
        if (!dataPermissionService.canAccessCreatedBy(plan.getCreateUserId())) {
            return Result.forbidden("无权查看该盘点计划");
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
        if (plan.getCreateUserId() == null) {
            plan.setCreateUserId(dataPermissionService.getCurrentUserId());
        }
        if (plan.getCreateUserName() == null || plan.getCreateUserName().isBlank()) {
            var currentUser = dataPermissionService.getCurrentUser();
            if (currentUser != null) {
                plan.setCreateUserName(currentUser.getRealName() != null && !currentUser.getRealName().isBlank()
                    ? currentUser.getRealName()
                    : currentUser.getUsername());
            }
        }

        inventoryPlanMapper.insert(plan);
        Result<Void> result = Result.success("盘点计划创建成功", null);
        operationLogService.record("INVENTORY", "CREATE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "创建盘点计划：" + plan.getPlanNo(), result);
        return result;
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
        Result<Void> result = Result.success("盘点计划更新成功", null);
        operationLogService.record("INVENTORY", "UPDATE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "更新盘点计划：" + existing.getPlanNo(), result);
        return result;
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
        Result<Void> result = Result.success("盘点计划删除成功", null);
        operationLogService.record("INVENTORY", "DELETE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "删除盘点计划：" + plan.getPlanNo(), result);
        return result;
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
        Result<Void> result = Result.success("盘点计划已启用", null);
        operationLogService.record("INVENTORY", "ENABLE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "启用盘点计划：" + plan.getPlanNo(), result);
        return result;
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
        Result<Void> result = Result.success("盘点计划已停用", null);
        operationLogService.record("INVENTORY", "DISABLE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "停用盘点计划：" + plan.getPlanNo(), result);
        return result;
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

        InventoryTask task = buildTaskFromPlan(plan);
        inventoryTaskMapper.insert(task);

        Result<Void> result = Result.success("盘点计划执行成功，已生成盘点任务", null);
        operationLogService.record("INVENTORY", "EXECUTE_PLAN", "inventory_plan",
            String.valueOf(plan.getId()), "执行盘点计划：" + plan.getPlanNo() + "，生成任务：" + task.getTaskNo(), result);
        return result;
    }

    private InventoryTask buildTaskFromPlan(InventoryPlan plan) {
        LocalDate today = LocalDate.now();
        LocalDate taskStartDate = plan.getStartDate() != null && plan.getStartDate().isAfter(today)
            ? plan.getStartDate()
            : today;

        LocalDate taskEndDate = plan.getEndDate();
        if (taskEndDate == null || taskEndDate.isBefore(taskStartDate)) {
            taskEndDate = taskStartDate;
        }

        InventoryTask task = new InventoryTask();
        task.setTaskNo(generateTaskNo());
        task.setTaskName(generateTaskName(plan, taskStartDate));
        task.setTaskType(plan.getPlanType() == null ? 1 : plan.getPlanType());
        task.setCategoryIds(plan.getCategoryIds());
        task.setLocationIds(plan.getLocationIds());
        task.setStartDate(taskStartDate);
        task.setEndDate(taskEndDate);
        task.setStatus(0);
        task.setCreateUserId(plan.getCreateUserId());
        task.setRemark(buildTaskRemark(plan, taskStartDate));
        return task;
    }

    private String generateTaskName(InventoryPlan plan, LocalDate taskStartDate) {
        return plan.getPlanName() + "-" + taskStartDate.format(DateTimeFormatter.ISO_DATE) + "盘点任务";
    }

    private String buildTaskRemark(InventoryPlan plan, LocalDate taskStartDate) {
        StringBuilder remarkBuilder = new StringBuilder();
        remarkBuilder.append("由盘点计划[").append(plan.getPlanNo()).append("]执行生成");

        if (plan.getCategoryIds() != null && !plan.getCategoryIds().isBlank()) {
            remarkBuilder.append("；分类范围: ").append(plan.getCategoryIds());
        }
        if (plan.getLocationIds() != null && !plan.getLocationIds().isBlank()) {
            remarkBuilder.append("；位置范围: ").append(plan.getLocationIds());
        }
        if (plan.getRemark() != null && !plan.getRemark().isBlank()) {
            remarkBuilder.append("；计划备注: ").append(plan.getRemark());
        }
        remarkBuilder.append("；执行日期: ").append(taskStartDate.format(DateTimeFormatter.ISO_DATE));
        return remarkBuilder.toString();
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

    /**
     * 生成任务编号
     * 格式：PD + yyyyMMddHHmmss + 4 位随机数
     */
    private String generateTaskNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) ((Math.random() * 9000) + 1000);
        return "PD" + timestamp + random;
    }

    private Result<Map<String, Object>> applyCreatorScope(LambdaQueryWrapper<InventoryPlan> wrapper) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return null;
        }
        Long currentUserId = dataPermissionService.getCurrentUserId();
        if (currentUserId == null) {
            return Result.forbidden("当前用户无权查看盘点计划");
        }
        wrapper.eq(InventoryPlan::getCreateUserId, currentUserId);
        return null;
    }
}
