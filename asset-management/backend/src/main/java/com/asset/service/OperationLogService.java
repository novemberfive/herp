package com.asset.service;

import com.asset.dto.Result;
import com.asset.dto.PageResult;
import com.asset.entity.OperationLog;
import com.asset.entity.SysUser;
import com.asset.repository.OperationLogMapper;
import com.asset.repository.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.asset.util.UserContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 操作审计日志服务
 */
@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final SysUserMapper sysUserMapper;

    public OperationLogService(OperationLogMapper operationLogMapper, SysUserMapper sysUserMapper) {
        this.operationLogMapper = operationLogMapper;
        this.sysUserMapper = sysUserMapper;
    }

    public Result<PageResult<OperationLog>> getLogPage(String module,
                                                       String action,
                                                       String operatorUsername,
                                                       String requestUri,
                                                       Integer status,
                                                       String startDate,
                                                       String endDate,
                                                       Integer pageNum,
                                                       Integer pageSize) {
        int current = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null ? 20 : Math.min(Math.max(pageSize, 1), 100);

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(module), OperationLog::getModule, module != null ? module.trim() : null)
                .like(StringUtils.hasText(action), OperationLog::getAction, action != null ? action.trim() : null)
                .like(StringUtils.hasText(operatorUsername), OperationLog::getOperatorUsername, operatorUsername != null ? operatorUsername.trim() : null)
                .like(StringUtils.hasText(requestUri), OperationLog::getRequestUri, requestUri != null ? requestUri.trim() : null)
                .eq(status != null, OperationLog::getStatus, status)
                .ge(resolveStartDateTime(startDate) != null, OperationLog::getCreateTime, resolveStartDateTime(startDate))
                .le(resolveEndDateTime(endDate) != null, OperationLog::getCreateTime, resolveEndDateTime(endDate))
                .orderByDesc(OperationLog::getCreateTime)
                .orderByDesc(OperationLog::getId);

        Page<OperationLog> page = operationLogMapper.selectPage(new Page<>(current, size), wrapper);
        return Result.success(new PageResult<>(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    @Async
    public void record(String module,
                       String action,
                       String bizType,
                       String bizId,
                       String description,
                       Result<?> result) {
        try {
            OperationLog log = new OperationLog();
            log.setModule(trim(module, 50));
            log.setAction(trim(action, 50));
            log.setBizType(trim(bizType, 50));
            log.setBizId(trim(bizId, 100));
            log.setDescription(trim(description, 500));
            log.setStatus(isSuccess(result) ? 1 : 0);
            log.setResultCode(result == null ? null : result.getCode());
            log.setResultMessage(trim(result == null ? null : result.getMessage(), 255));
            log.setCreateTime(LocalDateTime.now());
            log.setDeleted(0);

            fillOperator(log);
            fillRequest(log);

            operationLogMapper.insert(log);
        } catch (Exception ignored) {
            // 审计日志不应阻塞主流程
        }
    }

    private void fillOperator(OperationLog log) {
        String username = UserContextUtil.getCurrentUsername();
        if (!StringUtils.hasText(username)) {
            return;
        }

        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            log.setOperatorUsername(trim(username, 50));
            return;
        }

        log.setOperatorId(user.getId());
        log.setOperatorUsername(trim(user.getUsername(), 50));
        log.setOperatorName(trim(user.getRealName(), 50));
        log.setOperatorRole(trim(user.getRole(), 20));
    }

    private void fillRequest(OperationLog log) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        log.setRequestMethod(trim(request.getMethod(), 10));
        log.setRequestUri(trim(request.getRequestURI(), 255));
        log.setIpAddress(trim(resolveIp(request), 64));
    }

    private boolean isSuccess(Result<?> result) {
        return result != null && result.getCode() == 200;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String trim(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private LocalDateTime resolveStartDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return LocalDate.parse(value.trim()).atStartOfDay();
    }

    private LocalDateTime resolveEndDateTime(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return LocalDate.parse(value.trim()).atTime(LocalTime.MAX);
    }
}
