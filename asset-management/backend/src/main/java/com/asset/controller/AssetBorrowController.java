package com.asset.controller;

import com.asset.dto.Result;
import com.asset.entity.AssetBorrow;
import com.asset.service.AssetBorrowService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产借用控制器
 */
@RestController
@RequestMapping("/asset/borrow")
public class AssetBorrowController {

    private final AssetBorrowService assetBorrowService;

    public AssetBorrowController(AssetBorrowService assetBorrowService) {
        this.assetBorrowService = assetBorrowService;
    }

    /**
     * 分页查询资产借用列表
     * GET /api/asset/borrow/list?pageNum=1&pageSize=10&borrowStatus=1&borrowerId=1
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getBorrowList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer borrowStatus,
            @RequestParam(required = false) Long borrowerId) {
        return assetBorrowService.getBorrowList(pageNum, pageSize, borrowStatus, borrowerId);
    }

    /**
     * 根据 ID 查询资产借用详情
     * GET /api/asset/borrow/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<AssetBorrow> getBorrowById(@PathVariable Long id) {
        return assetBorrowService.getBorrowById(id);
    }

    /**
     * 创建资产借用申请
     * POST /api/asset/borrow
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> createBorrow(@Valid @RequestBody AssetBorrow borrow) {
        return assetBorrowService.createBorrow(borrow);
    }

    /**
     * 更新资产借用申请
     * PUT /api/asset/borrow
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> updateBorrow(@Valid @RequestBody AssetBorrow borrow) {
        return assetBorrowService.updateBorrow(borrow);
    }

    /**
     * 删除资产借用申请
     * DELETE /api/asset/borrow/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteBorrow(@PathVariable Long id) {
        return assetBorrowService.deleteBorrow(id);
    }

    /**
     * 审批资产借用申请
     * POST /api/asset/borrow/{id}/approve?approverId=1&approverName=张三&approveStatus=1&approveRemark=同意
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> approveBorrow(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam Integer approveStatus,
            @RequestParam(required = false) String approveRemark) {
        return assetBorrowService.approveBorrow(id, approverId, approverName, approveStatus, approveRemark);
    }

    /**
     * 归还资产
     * POST /api/asset/borrow/{id}/return?returnCondition=良好&returnRemark=xxx
     */
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> returnAsset(
            @PathVariable Long id,
            @RequestParam(required = false) String returnCondition,
            @RequestParam(required = false) String returnRemark) {
        return assetBorrowService.returnAsset(id, returnCondition, returnRemark);
    }

    /**
     * 取消借用
     * POST /api/asset/borrow/{id}/cancel?reason=xxx
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> cancelBorrow(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return assetBorrowService.cancelBorrow(id, reason);
    }

    /**
     * 发送逾期提醒
     * POST /api/asset/borrow/{id}/remind
     */
    @PostMapping("/{id}/remind")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Result<Void> sendOverdueReminder(@PathVariable Long id) {
        return assetBorrowService.sendOverdueReminder(id);
    }
}
