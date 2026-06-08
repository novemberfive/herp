package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.PurchaseRequest;
import com.asset.service.PurchaseRequestService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 采购申请控制器
 */
@RestController
@RequestMapping("/acquisition/purchase")
public class PurchaseRequestController {
    
    private final PurchaseRequestService purchaseRequestService;
    
    public PurchaseRequestController(PurchaseRequestService purchaseRequestService) {
        this.purchaseRequestService = purchaseRequestService;
    }
    
    /**
     * 分页查询采购申请列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('acquisition:purchase')")
    public Result<PageResult<PurchaseRequest>> getPageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            PurchaseRequest query) {
        return purchaseRequestService.getPageList(pageNum, pageSize, query);
    }
    
    /**
     * 根据 ID 查询采购申请
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:purchase')")
    public Result<PurchaseRequest> getById(@PathVariable Long id) {
        return purchaseRequestService.getById(id);
    }
    
    /**
     * 创建采购申请
     */
    @PostMapping
    @PreAuthorize("hasAuthority('acquisition:purchase:create')")
    public Result<Void> create(@Valid @RequestBody PurchaseRequest request) {
        return purchaseRequestService.create(request);
    }
    
    /**
     * 更新采购申请
     */
    @PutMapping
    @PreAuthorize("hasAuthority('acquisition:purchase:edit')")
    public Result<Void> update(@Valid @RequestBody PurchaseRequest request) {
        return purchaseRequestService.update(request);
    }
    
    /**
     * 删除采购申请
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('acquisition:purchase:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        return purchaseRequestService.delete(id);
    }
    
    /**
     * 提交审批
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('acquisition:purchase:submit')")
    public Result<Void> submit(@PathVariable Long id) {
        return purchaseRequestService.submit(id);
    }
    
    /**
     * 审批通过
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('acquisition:purchase:approve')")
    public Result<Void> approve(@PathVariable Long id, @RequestParam String opinion) {
        return purchaseRequestService.approve(id, opinion);
    }
    
    /**
     * 审批拒绝
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('acquisition:purchase:approve')")
    public Result<Void> reject(@PathVariable Long id, @RequestParam String opinion) {
        return purchaseRequestService.reject(id, opinion);
    }
}
