package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetDisposal;
import com.asset.service.AssetDisposalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 资产处置控制器
 */
@RestController
@RequestMapping("/asset/disposal")
public class AssetDisposalController {

    private final AssetDisposalService assetDisposalService;

    public AssetDisposalController(AssetDisposalService assetDisposalService) {
        this.assetDisposalService = assetDisposalService;
    }

    /**
     * 分页查询资产处置列表
     * GET /api/asset/disposal/list?pageNum=1&pageSize=10&disposalType=1&approveStatus=1&disposalStatus=1
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getDisposalList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer disposalType,
            @RequestParam(required = false) Integer approveStatus,
            @RequestParam(required = false) Integer disposalStatus) {
        return assetDisposalService.getDisposalList(pageNum, pageSize, disposalType, approveStatus, disposalStatus);
    }

    /**
     * 根据 ID 查询资产处置详情
     * GET /api/asset/disposal/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<AssetDisposal> getDisposalById(@PathVariable Long id) {
        return assetDisposalService.getDisposalById(id);
    }

    /**
     * 创建资产处置申请
     * POST /api/asset/disposal
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createDisposal(@Valid @RequestBody AssetDisposal disposal) {
        return assetDisposalService.createDisposal(disposal);
    }

    /**
     * 更新资产处置申请
     * PUT /api/asset/disposal
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateDisposal(@Valid @RequestBody AssetDisposal disposal) {
        return assetDisposalService.updateDisposal(disposal);
    }

    /**
     * 删除资产处置申请
     * DELETE /api/asset/disposal/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteDisposal(@PathVariable Long id) {
        return assetDisposalService.deleteDisposal(id);
    }

    /**
     * 审批资产处置申请
     * POST /api/asset/disposal/{id}/approve?approverId=1&approverName=张三&approveStatus=1&approveRemark=同意
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> approveDisposal(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam Integer approveStatus,
            @RequestParam(required = false) String approveRemark) {
        return assetDisposalService.approveDisposal(id, approverId, approverName, approveStatus, approveRemark);
    }

    /**
     * 执行资产处置
     * POST /api/asset/disposal/{id}/execute?disposalMethod=1&actualValue=1000.00&buyerName=xxx&buyerContact=xxx
     */
    @PostMapping("/{id}/execute")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> executeDisposal(
            @PathVariable Long id,
            @RequestParam Integer disposalMethod,
            @RequestParam(required = false) BigDecimal actualValue,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String buyerContact) {
        return assetDisposalService.executeDisposal(id, disposalMethod, actualValue, buyerName, buyerContact);
    }

    /**
     * 完成资产处置
     * POST /api/asset/disposal/{id}/complete
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> completeDisposal(@PathVariable Long id) {
        return assetDisposalService.completeDisposal(id);
    }

    /**
     * 取消资产处置
     * POST /api/asset/disposal/{id}/cancel?reason=xxx
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> cancelDisposal(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return assetDisposalService.cancelDisposal(id, reason);
    }
}
