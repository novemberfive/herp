package com.asset.controller;

import com.asset.dto.PageResult;
import com.asset.dto.Result;
import com.asset.entity.OperationLog;
import com.asset.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志查询控制器
 */
@RestController
@RequestMapping("/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:role')")
    public Result<PageResult<OperationLog>> getLogPage(@RequestParam(required = false) String module,
                                                       @RequestParam(required = false) String action,
                                                       @RequestParam(required = false) String operatorUsername,
                                                       @RequestParam(required = false) String requestUri,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate,
                                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                                       @RequestParam(defaultValue = "20") Integer pageSize) {
        return operationLogService.getLogPage(module, action, operatorUsername, requestUri, status, startDate, endDate, pageNum, pageSize);
    }
}
