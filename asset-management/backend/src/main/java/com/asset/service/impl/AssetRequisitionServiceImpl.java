package com.asset.service.impl;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.AssetRequisition;
import com.asset.repository.AssetRequisitionRepository;
import com.asset.repository.SysUserMapper;
import com.asset.service.AssetRequisitionService;
import com.asset.service.DataPermissionService;
import com.asset.service.OperationLogService;
import com.asset.util.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 资产领用退库服务实现类
 */
@Service
public class AssetRequisitionServiceImpl implements AssetRequisitionService {
    
    private final AssetRequisitionRepository assetRequisitionRepository;
    private final SysUserMapper sysUserMapper;
    private final DataPermissionService dataPermissionService;
    private final OperationLogService operationLogService;
    
    public AssetRequisitionServiceImpl(AssetRequisitionRepository assetRequisitionRepository,
                                       SysUserMapper sysUserMapper,
                                       DataPermissionService dataPermissionService,
                                       OperationLogService operationLogService) {
        this.assetRequisitionRepository = assetRequisitionRepository;
        this.sysUserMapper = sysUserMapper;
        this.dataPermissionService = dataPermissionService;
        this.operationLogService = operationLogService;
    }
    
    @Override
    public Result<PageResult<AssetRequisition>> getPageList(Integer pageNum, Integer pageSize, AssetRequisition query) {
        Page<AssetRequisition> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AssetRequisition> wrapper = new LambdaQueryWrapper<>();
        
        if (query != null) {
            if (query.getRequisitionNo() != null && !query.getRequisitionNo().isEmpty()) {
                wrapper.like(AssetRequisition::getRequisitionNo, query.getRequisitionNo());
            }
            if (query.getAssetCode() != null && !query.getAssetCode().isEmpty()) {
                wrapper.like(AssetRequisition::getAssetCode, query.getAssetCode());
            }
            if (query.getAssetName() != null && !query.getAssetName().isEmpty()) {
                wrapper.like(AssetRequisition::getAssetName, query.getAssetName());
            }
            if (query.getBusinessType() != null) {
                wrapper.eq(AssetRequisition::getBusinessType, query.getBusinessType());
            }
            if (query.getStatus() != null) {
                wrapper.eq(AssetRequisition::getStatus, query.getStatus());
            }
            if (query.getCategoryId() != null) {
                wrapper.eq(AssetRequisition::getCategoryId, query.getCategoryId());
            }
        }
        
        wrapper.eq(AssetRequisition::getDeleted, 0);
        Result<PageResult<AssetRequisition>> scopeResult = applyDepartmentScope(wrapper);
        if (scopeResult != null) {
            return scopeResult;
        }
        wrapper.orderByDesc(AssetRequisition::getCreateTime);
        
        Page<AssetRequisition> resultPage = assetRequisitionRepository.selectPage(page, wrapper);
        PageResult<AssetRequisition> pageResult = new PageResult<>(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getSize(),
            resultPage.getCurrent()
        );
        
        return Result.success(pageResult);
    }
    
    @Override
    public Result<AssetRequisition> getById(Long id) {
        AssetRequisition requisition = assetRequisitionRepository.selectById(id);
        if (requisition == null || requisition.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (!canAccessRequisition(requisition)) {
            return Result.forbidden("无权查看该领用退库记录");
        }
        return Result.success(requisition);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(AssetRequisition requisition) {
        requisition.setRequisitionNo(generateRequisitionNo());
        requisition.setRequisitionDate(LocalDateTime.now());
        if (requisition.getStatus() == null) {
            requisition.setStatus(0); // 草稿
        }
        if (requisition.getBusinessType() == 1) {
            // 领用类型，计算总金额
            if (requisition.getUnitPrice() != null && requisition.getQuantity() != null) {
                requisition.setTotalAmount(requisition.getUnitPrice().multiply(new java.math.BigDecimal(requisition.getQuantity())));
            }
        } else if (requisition.getBusinessType() == 2) {
            // 退库类型
            requisition.setActualReturnDate(LocalDateTime.now());
        }
        assetRequisitionRepository.insert(requisition);
        Result<Void> result = Result.success("资产领用退库创建成功", null);
        operationLogService.record("MANAGEMENT", "CREATE_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "创建领用退库单：" + requisition.getRequisitionNo(), result);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(AssetRequisition requisition) {
        AssetRequisition existing = assetRequisitionRepository.selectById(requisition.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (existing.getStatus() != 0) {
            return Result.error("非草稿状态的记录不能修改");
        }
        requisition.setUpdateTime(LocalDateTime.now());
        assetRequisitionRepository.updateById(requisition);
        Result<Void> result = Result.success("资产领用退库更新成功", null);
        operationLogService.record("MANAGEMENT", "UPDATE_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "更新领用退库单：" + existing.getRequisitionNo(), result);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(Long id) {
        AssetRequisition requisition = assetRequisitionRepository.selectById(id);
        if (requisition == null || requisition.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (requisition.getStatus() != 0) {
            return Result.error("非草稿状态的记录不能删除");
        }
        requisition.setDeleted(1);
        assetRequisitionRepository.updateById(requisition);
        Result<Void> result = Result.success("资产领用退库记录删除成功", null);
        operationLogService.record("MANAGEMENT", "DELETE_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "删除领用退库单：" + requisition.getRequisitionNo(), result);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> submit(Long id) {
        AssetRequisition requisition = assetRequisitionRepository.selectById(id);
        if (requisition == null || requisition.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (requisition.getStatus() != 0) {
            return Result.error("只有草稿状态的记录可以提交");
        }
        
        requisition.setStatus(1); // 待审批
        requisition.setUpdateTime(LocalDateTime.now());
        assetRequisitionRepository.updateById(requisition);
        Result<Void> result = Result.success("提交审批成功", null);
        operationLogService.record("MANAGEMENT", "SUBMIT_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "提交领用退库单：" + requisition.getRequisitionNo(), result);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approve(Long id, String opinion) {
        AssetRequisition requisition = assetRequisitionRepository.selectById(id);
        if (requisition == null || requisition.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (requisition.getStatus() != 1) {
            return Result.error("只有待审批状态的记录可以审批");
        }
        
        requisition.setStatus(2); // 审批通过
        requisition.setApproveOpinion(opinion);
        requisition.setApproveTime(LocalDateTime.now());
        
        // 获取当前登录用户信息设置审批人
        String username = UserContextUtil.getCurrentUsername();
        if (username != null) {
            requisition.setApproverId(getUserIdByUsername(username));
            requisition.setApproverName(username);
        }
        
        requisition.setUpdateTime(LocalDateTime.now());
        assetRequisitionRepository.updateById(requisition);
        Result<Void> result = Result.success("审批通过", null);
        operationLogService.record("MANAGEMENT", "APPROVE_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "审批通过领用退库单：" + requisition.getRequisitionNo(), result);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> reject(Long id, String opinion) {
        AssetRequisition requisition = assetRequisitionRepository.selectById(id);
        if (requisition == null || requisition.getDeleted() == 1) {
            return Result.error("资产领用退库记录不存在");
        }
        if (requisition.getStatus() != 1) {
            return Result.error("只有待审批状态的记录可以审批");
        }
        
        requisition.setStatus(3); // 审批拒绝
        requisition.setApproveOpinion(opinion);
        requisition.setApproveTime(LocalDateTime.now());
        
        // 获取当前登录用户信息设置审批人
        String username = UserContextUtil.getCurrentUsername();
        if (username != null) {
            requisition.setApproverId(getUserIdByUsername(username));
            requisition.setApproverName(username);
        }
        
        requisition.setUpdateTime(LocalDateTime.now());
        assetRequisitionRepository.updateById(requisition);
        Result<Void> result = Result.success("审批拒绝", null);
        operationLogService.record("MANAGEMENT", "REJECT_REQUISITION", "asset_requisition",
            String.valueOf(requisition.getId()), "审批拒绝领用退库单：" + requisition.getRequisitionNo(), result);
        return result;
    }
    
    /**
     * 根据用户名获取用户 ID
     */
    private Long getUserIdByUsername(String username) {
        if (username == null) {
            return null;
        }
        com.asset.entity.SysUser user = sysUserMapper.selectByUsername(username);
        return user != null ? user.getId() : null;
    }
    
    private String generateRequisitionNo() {
        return "RQ" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private Result<PageResult<AssetRequisition>> applyDepartmentScope(LambdaQueryWrapper<AssetRequisition> wrapper) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return null;
        }
        Long departmentId = dataPermissionService.getCurrentDepartmentId();
        if (departmentId == null) {
            return Result.forbidden("当前用户未绑定部门，无法查看领用退库记录");
        }
        wrapper.and(scope -> scope
            .eq(AssetRequisition::getOriginalDepartmentId, departmentId)
            .or()
            .eq(AssetRequisition::getNewDepartmentId, departmentId));
        return null;
    }

    private boolean canAccessRequisition(AssetRequisition requisition) {
        if (!dataPermissionService.shouldRestrictToOwnDepartment()) {
            return true;
        }
        return dataPermissionService.canAccessDepartment(requisition.getOriginalDepartmentId())
            || dataPermissionService.canAccessDepartment(requisition.getNewDepartmentId());
    }
}
