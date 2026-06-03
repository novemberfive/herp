package com.asset.service.impl;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.PurchaseRequest;
import com.asset.repository.PurchaseRequestRepository;
import com.asset.repository.SysUserMapper;
import com.asset.service.PurchaseRequestService;
import com.asset.util.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 采购申请服务实现类
 */
@Service
public class PurchaseRequestServiceImpl implements PurchaseRequestService {
    
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final SysUserMapper sysUserMapper;
    
    public PurchaseRequestServiceImpl(PurchaseRequestRepository purchaseRequestRepository, SysUserMapper sysUserMapper) {
        this.purchaseRequestRepository = purchaseRequestRepository;
        this.sysUserMapper = sysUserMapper;
    }
    
    @Override
    public Result<PageResult<PurchaseRequest>> getPageList(Integer pageNum, Integer pageSize, PurchaseRequest query) {
        Page<PurchaseRequest> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PurchaseRequest> wrapper = new LambdaQueryWrapper<>();
        
        if (query != null) {
            if (query.getRequestNo() != null && !query.getRequestNo().isEmpty()) {
                wrapper.like(PurchaseRequest::getRequestNo, query.getRequestNo());
            }
            if (query.getAssetName() != null && !query.getAssetName().isEmpty()) {
                wrapper.like(PurchaseRequest::getAssetName, query.getAssetName());
            }
            if (query.getStatus() != null) {
                wrapper.eq(PurchaseRequest::getStatus, query.getStatus());
            }
            if (query.getApplicantId() != null) {
                wrapper.eq(PurchaseRequest::getApplicantId, query.getApplicantId());
            }
        }
        
        wrapper.eq(PurchaseRequest::getDeleted, 0);
        wrapper.orderByDesc(PurchaseRequest::getCreateTime);
        
        Page<PurchaseRequest> resultPage = purchaseRequestRepository.selectPage(page, wrapper);
        PageResult<PurchaseRequest> pageResult = new PageResult<>(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getSize(),
            resultPage.getCurrent()
        );
        
        return Result.success(pageResult);
    }
    
    @Override
    public Result<PurchaseRequest> getById(Long id) {
        PurchaseRequest request = purchaseRequestRepository.selectById(id);
        if (request == null || request.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        return Result.success(request);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> create(PurchaseRequest request) {
        request.setRequestNo(generateRequestNo());
        request.setApplyDate(LocalDateTime.now());
        request.setStatus(0);
        purchaseRequestRepository.insert(request);
        return Result.success("采购申请创建成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(PurchaseRequest request) {
        PurchaseRequest existing = purchaseRequestRepository.selectById(request.getId());
        if (existing == null || existing.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        if (existing.getStatus() != 0) {
            return Result.error("非草稿状态的采购申请不能修改");
        }
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestRepository.updateById(request);
        return Result.success("采购申请更新成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> delete(Long id) {
        PurchaseRequest request = purchaseRequestRepository.selectById(id);
        if (request == null || request.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        request.setDeleted(1);
        purchaseRequestRepository.updateById(request);
        return Result.success("采购申请删除成功", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> submit(Long id) {
        PurchaseRequest request = purchaseRequestRepository.selectById(id);
        if (request == null || request.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        if (request.getStatus() != 0) {
            return Result.error("只有草稿状态的采购申请可以提交");
        }
        request.setStatus(1);
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestRepository.updateById(request);
        return Result.success("采购申请已提交审批", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> approve(Long id, String opinion) {
        PurchaseRequest request = purchaseRequestRepository.selectById(id);
        if (request == null || request.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        if (request.getStatus() != 1) {
            return Result.error("只有待审批状态的采购申请可以审批");
        }
        
        // 获取当前登录用户信息设置审批人
        String username = UserContextUtil.getCurrentUsername();
        if (username != null) {
            request.setApproverId(getUserIdByUsername(username));
            request.setApproverName(username);
        }
        
        request.setStatus(2);
        request.setApproveOpinion(opinion);
        request.setApproveTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestRepository.updateById(request);
        return Result.success("采购申请审批通过", null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> reject(Long id, String opinion) {
        PurchaseRequest request = purchaseRequestRepository.selectById(id);
        if (request == null || request.getDeleted() == 1) {
            return Result.error("采购申请不存在");
        }
        if (request.getStatus() != 1) {
            return Result.error("只有待审批状态的采购申请可以审批");
        }
        
        // 获取当前登录用户信息设置审批人
        String username = UserContextUtil.getCurrentUsername();
        if (username != null) {
            request.setApproverId(getUserIdByUsername(username));
            request.setApproverName(username);
        }
        
        request.setStatus(3);
        request.setApproveOpinion(opinion);
        request.setApproveTime(LocalDateTime.now());
        request.setUpdateTime(LocalDateTime.now());
        purchaseRequestRepository.updateById(request);
        return Result.success("采购申请审批拒绝", null);
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
    
    private String generateRequestNo() {
        return "PR" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
