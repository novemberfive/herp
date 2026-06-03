package com.asset.controller;

import com.asset.dto.Result;
import com.asset.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产报表控制器
 */
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 获取资产统计报表
     * GET /api/reports/statistics?startDate=2024-01-01&endDate=2024-12-31&departmentId=1
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getStatisticsReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId) {
        return reportService.getStatisticsReport(startDate, endDate, departmentId);
    }

    /**
     * 获取资产处置报表
     * GET /api/reports/disposal?startDate=2024-01-01&endDate=2024-12-31&disposalType=1&pageNum=1&pageSize=10
     */
    @GetMapping("/disposal")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getDisposalReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer disposalType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return reportService.getDisposalReport(startDate, endDate, disposalType, pageNum, pageSize);
    }

    /**
     * 获取资产折旧报表
     * GET /api/reports/depreciation?startDate=2024-01-01&endDate=2024-12-31&categoryId=1&pageNum=1&pageSize=10
     */
    @GetMapping("/depreciation")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'VIEWER')")
    public Result<Map<String, Object>> getDepreciationReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return reportService.getDepreciationReport(startDate, endDate, categoryId, pageNum, pageSize);
    }

    /**
     * 导出资产统计报表
     * GET /api/reports/export/statistics?startDate=2024-01-01&endDate=2024-12-31&departmentId=1
     */
    @GetMapping("/export/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public void exportStatisticsReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long departmentId,
            HttpServletResponse response) {
        reportService.exportStatisticsReport(startDate, endDate, departmentId, response);
    }

    /**
     * 导出资产处置报表
     * GET /api/reports/export/disposal?startDate=2024-01-01&endDate=2024-12-31&disposalType=1
     */
    @GetMapping("/export/disposal")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public void exportDisposalReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer disposalType,
            HttpServletResponse response) {
        reportService.exportDisposalReport(startDate, endDate, disposalType, response);
    }

    /**
     * 导出资产折旧报表
     * GET /api/reports/export/depreciation?startDate=2024-01-01&endDate=2024-12-31&categoryId=1
     */
    @GetMapping("/export/depreciation")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public void exportDepreciationReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long categoryId,
            HttpServletResponse response) {
        reportService.exportDepreciationReport(startDate, endDate, categoryId, response);
    }
}
