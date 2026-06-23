package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetTransfer;
import com.asset.service.AssetTransferService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产调拨控制器
 */
@RestController
@RequestMapping("/transfers")
public class AssetTransferController {

    private final AssetTransferService assetTransferService;

    public AssetTransferController(AssetTransferService assetTransferService) {
        this.assetTransferService = assetTransferService;
    }

    /**
     * 分页查询调拨列表
     * GET /api/transfers/list?pageNum=1&pageSize=10&approveStatus=1&transferStatus=2
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('management:transfer')")
    public Result<Map<String, Object>> getTransferList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer approveStatus,
            @RequestParam(required = false) Integer transferStatus) {
        return assetTransferService.getTransferList(pageNum, pageSize, approveStatus, transferStatus);
    }

    /**
     * 根据 ID 查询调拨详情
     * GET /api/transfers/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('management:transfer')")
    public Result<AssetTransfer> getTransferById(@PathVariable Long id) {
        return assetTransferService.getTransferById(id);
    }

    /**
     * 创建调拨申请
     * POST /api/transfers
     */
    @PostMapping
    @PreAuthorize("hasAuthority('management:transfer:create')")
    public Result<Void> createTransfer(@Valid @RequestBody AssetTransfer transfer) {
        return assetTransferService.createTransfer(transfer);
    }

    /**
     * 更新调拨信息
     * PUT /api/transfers
     */
    @PutMapping
    @PreAuthorize("hasAuthority('management:transfer:edit')")
    public Result<Void> updateTransfer(@Valid @RequestBody AssetTransfer transfer) {
        return assetTransferService.updateTransfer(transfer);
    }

    /**
     * 删除调拨记录
     * DELETE /api/transfers/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('management:transfer:delete')")
    public Result<Void> deleteTransfer(@PathVariable Long id) {
        return assetTransferService.deleteTransfer(id);
    }

    /**
     * 审批调拨申请
     * POST /api/transfers/{id}/approve
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('management:transfer:approve')")
    public Result<Void> approveTransfer(
            @PathVariable Long id,
            @RequestParam Integer approveStatus,
            @RequestParam(required = false) String approveRemark) {
        // 从安全上下文中获取当前用户 ID
        String username = com.asset.util.UserContextUtil.getCurrentUsername();
        Long approverId = null;
        if (username != null) {
            // 这里简化处理，实际应该通过 UserService 获取
            approverId = 1L; // 临时使用固定值，后续可通过 sysUserMapper 查询
        }
        return assetTransferService.approveTransfer(id, approveStatus, approveRemark, approverId);
    }

    /**
     * 完成调拨
     * POST /api/transfers/{id}/complete
     */
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('management:transfer:complete')")
    public Result<Void> completeTransfer(@PathVariable Long id) {
        return assetTransferService.completeTransfer(id);
    }

    /**
     * 取消调拨
     * POST /api/transfers/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('management:transfer:cancel')")
    public Result<Void> cancelTransfer(@PathVariable Long id) {
        return assetTransferService.cancelTransfer(id);
    }
}
