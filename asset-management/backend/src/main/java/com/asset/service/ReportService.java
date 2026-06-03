package com.asset.service;

import com.asset.dto.Result;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 资产报表服务类
 */
public interface ReportService {

    /**
     * 获取资产统计报表数据
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param departmentId 部门 ID（可选）
     * @return 统计报表数据
     */
    Result<Map<String, Object>> getStatisticsReport(String startDate, String endDate, Long departmentId);

    /**
     * 获取资产处置报表数据
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param disposalType 处置类型（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 处置报表数据
     */
    Result<Map<String, Object>> getDisposalReport(String startDate, String endDate, Integer disposalType, Integer pageNum, Integer pageSize);

    /**
     * 获取资产折旧报表数据
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param categoryId 分类 ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 折旧报表数据
     */
    Result<Map<String, Object>> getDepreciationReport(String startDate, String endDate, Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 导出统计报表为 Excel
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param departmentId 部门 ID
     * @param response HTTP 响应
     */
    void exportStatisticsReport(String startDate, String endDate, Long departmentId, HttpServletResponse response);

    /**
     * 导出处置报表为 Excel
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param disposalType 处置类型
     * @param response HTTP 响应
     */
    void exportDisposalReport(String startDate, String endDate, Integer disposalType, HttpServletResponse response);

    /**
     * 导出折旧报表为 Excel
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param categoryId 分类 ID
     * @param response HTTP 响应
     */
    void exportDepreciationReport(String startDate, String endDate, Long categoryId, HttpServletResponse response);
}
