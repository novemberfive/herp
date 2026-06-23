package com.asset.service.impl;

import com.asset.dto.Result;
import com.asset.entity.InventoryTask;
import com.asset.repository.InventoryTaskMapper;
import com.asset.service.DataPermissionService;
import com.asset.service.OperationLogService;
import com.asset.service.InventoryTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 盘点任务服务实现类
 */
@Service
public class InventoryTaskServiceImpl implements InventoryTaskService {

    private final InventoryTaskMapper inventoryTaskMapper;
    private final DataPermissionService dataPermissionService;
    private final OperationLogService operationLogService;

    public InventoryTaskServiceImpl(InventoryTaskMapper inventoryTaskMapper,
                                    DataPermissionService dataPermissionService,
                                    OperationLogService operationLogService) {
        this.inventoryTaskMapper = inventoryTaskMapper;
        this.dataPermissionService = dataPermissionService;
        this.operationLogService = operationLogService;
    }

    @Override
    public Result<Map<String, Object>> getTaskList(Integer pageNum, Integer pageSize, Integer status, Integer taskType) {
        Page<InventoryTask> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<InventoryTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryTask::getDeleted, 0);
        
        if (status != null) {
            wrapper.eq(InventoryTask::getStatus, status);
        }
        
        if (taskType != null) {
            wrapper.eq(InventoryTask::getTaskType, taskType);
        }

        Result<Map<String, Object>> scopeResult = applyCreatorScope(wrapper);
        if (scopeResult != null) {
            return scopeResult;
        }

        wrapper.orderByDesc(InventoryTask::getCreateTime);
        
        Page<InventoryTask> resultPage = inventoryTaskMapper.selectPage(page, wrapper);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<InventoryTask> getTaskById(Long id) {
        InventoryTask task = inventoryTaskMapper.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        if (!dataPermissionService.canAccessCreatedBy(task.getCreateUserId())) {
            return Result.forbidden("无权查看该盘点任务");
        }
        return Result.success(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> createTask(InventoryTask task) {
        // 生成任务编号
        String taskNo = generateTaskNo();
        task.setTaskNo(taskNo);
        
        // 设置初始状态
        task.setStatus(0); // 未开始
        if (task.getCreateUserId() == null) {
            task.setCreateUserId(dataPermissionService.getCurrentUserId());
        }

        inventoryTaskMapper.insert(task);
        Result<Void> result = Result.success("盘点任务创建成功", null);
        operationLogService.record("INVENTORY", "CREATE_TASK", "inventory_task",
            String.valueOf(task.getId()), "创建盘点任务：" + task.getTaskNo(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateTask(InventoryTask task) {
        InventoryTask existing = inventoryTaskMapper.selectById(task.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        
        // 已完成或已终止的任务不能修改
        if (existing.getStatus() == 2 || existing.getStatus() == 3) {
            return Result.error("已完成或已终止的任务不能修改");
        }
        
        task.setUpdateTime(LocalDateTime.now());
        inventoryTaskMapper.updateById(task);
        Result<Void> result = Result.success("盘点任务更新成功", null);
        operationLogService.record("INVENTORY", "UPDATE_TASK", "inventory_task",
            String.valueOf(task.getId()), "更新盘点任务：" + existing.getTaskNo(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteTask(Long id) {
        InventoryTask task = inventoryTaskMapper.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        
        task.setDeleted(1);
        task.setUpdateTime(LocalDateTime.now());
        inventoryTaskMapper.updateById(task);
        Result<Void> result = Result.success("盘点任务删除成功", null);
        operationLogService.record("INVENTORY", "DELETE_TASK", "inventory_task",
            String.valueOf(task.getId()), "删除盘点任务：" + task.getTaskNo(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> startTask(Long id) {
        InventoryTask task = inventoryTaskMapper.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        
        if (task.getStatus() != 0) {
            return Result.error("只有未开始的任务才能启动");
        }
        
        task.setStatus(1); // 进行中
        task.setUpdateTime(LocalDateTime.now());
        inventoryTaskMapper.updateById(task);
        Result<Void> result = Result.success("盘点任务已启动", null);
        operationLogService.record("INVENTORY", "START_TASK", "inventory_task",
            String.valueOf(task.getId()), "启动盘点任务：" + task.getTaskNo(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> completeTask(Long id) {
        InventoryTask task = inventoryTaskMapper.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        
        if (task.getStatus() != 1) {
            return Result.error("只有进行中的任务才能完成");
        }
        
        task.setStatus(2); // 已完成
        task.setCompleteTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        inventoryTaskMapper.updateById(task);
        Result<Void> result = Result.success("盘点任务已完成", null);
        operationLogService.record("INVENTORY", "COMPLETE_TASK", "inventory_task",
            String.valueOf(task.getId()), "完成盘点任务：" + task.getTaskNo(), result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancelTask(Long id) {
        InventoryTask task = inventoryTaskMapper.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            return Result.error("盘点任务不存在");
        }
        
        if (task.getStatus() == 2) {
            return Result.error("已完成的任务不能终止");
        }
        
        task.setStatus(3); // 已终止
        task.setUpdateTime(LocalDateTime.now());
        inventoryTaskMapper.updateById(task);
        Result<Void> result = Result.success("盘点任务已终止", null);
        operationLogService.record("INVENTORY", "CANCEL_TASK", "inventory_task",
            String.valueOf(task.getId()), "终止盘点任务：" + task.getTaskNo(), result);
        return result;
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

    private Result<Map<String, Object>> applyCreatorScope(LambdaQueryWrapper<InventoryTask> wrapper) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return null;
        }
        Long currentUserId = dataPermissionService.getCurrentUserId();
        if (currentUserId == null) {
            return Result.forbidden("当前用户无权查看盘点任务");
        }
        wrapper.eq(InventoryTask::getCreateUserId, currentUserId);
        return null;
    }
}
