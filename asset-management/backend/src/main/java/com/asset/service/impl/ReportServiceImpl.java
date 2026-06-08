package com.asset.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.asset.dto.Result;
import com.asset.entity.AssetCard;
import com.asset.entity.AssetDisposal;
import com.asset.repository.AssetCardMapper;
import com.asset.repository.AssetDisposalMapper;
import com.asset.service.DataPermissionService;
import com.asset.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 资产报表服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final AssetCardMapper assetCardMapper;
    private final AssetDisposalMapper assetDisposalMapper;
    private final DataPermissionService dataPermissionService;

    public ReportServiceImpl(AssetCardMapper assetCardMapper,
                             AssetDisposalMapper assetDisposalMapper,
                             DataPermissionService dataPermissionService) {
        this.assetCardMapper = assetCardMapper;
        this.assetDisposalMapper = assetDisposalMapper;
        this.dataPermissionService = dataPermissionService;
    }

    @Override
    public Result<Map<String, Object>> getStatisticsReport(String startDate, String endDate, Long departmentId) {
        // 构建查询条件
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);

        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(departmentId);
        if (dataPermissionService.shouldRestrictToOwnDepartment()) {
            if (scopedDepartmentId == null) {
                wrapper.eq(AssetCard::getDepartmentId, -1L);
            } else {
                wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
            }
        } else if (scopedDepartmentId != null) {
            wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
        }
        
        // 日期范围筛选（基于创建时间）
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(AssetCard::getCreateTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            wrapper.le(AssetCard::getCreateTime, endDateTime);
        }
        
        List<AssetCard> allAssets = assetCardMapper.selectList(wrapper);
        
        // 统计总数
        long total = allAssets.size();
        
        // 按状态统计
        Map<Integer, Long> statusCount = allAssets.stream()
                .collect(Collectors.groupingBy(AssetCard::getStatus, Collectors.counting()));
        
        // 按分类统计
        Map<Long, Long> categoryCount = allAssets.stream()
                .filter(a -> a.getCategoryId() != null)
                .collect(Collectors.groupingBy(AssetCard::getCategoryId, Collectors.counting()));
        
        // 按部门统计
        Map<Long, Long> departmentCount = allAssets.stream()
                .filter(a -> a.getDepartmentId() != null)
                .collect(Collectors.groupingBy(AssetCard::getDepartmentId, Collectors.counting()));
        
        // 计算总金额
        BigDecimal totalAmount = allAssets.stream()
                .map(AssetCard::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 状态映射
        Map<String, Long> statusMap = new HashMap<>();
        statusMap.put("闲置", statusCount.getOrDefault(0, 0L));
        statusMap.put("在用", statusCount.getOrDefault(1, 0L));
        statusMap.put("维修中", statusCount.getOrDefault(2, 0L));
        statusMap.put("待处置", statusCount.getOrDefault(3, 0L));
        statusMap.put("已处置", statusCount.getOrDefault(4, 0L));
        statusMap.put("丢失", statusCount.getOrDefault(5, 0L));
        statusMap.put("借出", statusCount.getOrDefault(6, 0L));
        
        // 图表数据 - 状态分布
        List<Map<String, Object>> statusChartData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : statusMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            statusChartData.add(item);
        }
        
        // 图表数据 - 分类 TOP10
        List<Map<String, Object>> categoryChartData = new ArrayList<>();
        // 这里需要关联分类表获取分类名称，简化处理用 ID 代替
        
        // 图表数据 - 部门分布 TOP10
        List<Map<String, Object>> departmentChartData = new ArrayList<>();
        // 这里需要关联部门表获取部门名称，简化处理用 ID 代替
        
        // 趋势数据 - 近 12 个月新增资产
        List<Map<String, Object>> trendData = generateTrendData(allAssets);
        
        Map<String, Object> data = Map.of(
            "total", total,
            "totalAmount", totalAmount,
            "statusCounts", statusMap,
            "categoryCounts", categoryCount,
            "departmentCounts", departmentCount,
            "statusChart", statusChartData,
            "trendData", trendData
        );
        
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> getDisposalReport(String startDate, String endDate, Integer disposalType, Integer pageNum, Integer pageSize) {
        Page<AssetDisposal> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetDisposal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetDisposal::getDeleted, 0);
        applyDisposalDepartmentScope(wrapper);
        
        // 日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(AssetDisposal::getApplyTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            wrapper.le(AssetDisposal::getApplyTime, endDateTime);
        }
        
        // 处置类型筛选
        if (disposalType != null) {
            wrapper.eq(AssetDisposal::getDisposalType, disposalType);
        }
        
        wrapper.orderByDesc(AssetDisposal::getApplyTime);
        
        Page<AssetDisposal> resultPage = assetDisposalMapper.selectPage(page, wrapper);
        
        // 统计数据
        LambdaQueryWrapper<AssetDisposal> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(AssetDisposal::getDeleted, 0);
        applyDisposalDepartmentScope(countWrapper);
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
            countWrapper.ge(AssetDisposal::getApplyTime, startDateTime);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
            countWrapper.le(AssetDisposal::getApplyTime, endDateTime);
        }
        if (disposalType != null) {
            countWrapper.eq(AssetDisposal::getDisposalType, disposalType);
        }
        
        long totalCount = assetDisposalMapper.selectCount(countWrapper);
        
        // 统计处置金额
        List<AssetDisposal> allDisposals = assetDisposalMapper.selectList(countWrapper);
        BigDecimal totalActualValue = allDisposals.stream()
                .filter(d -> d.getDisposalStatus() == 2) // 已完成
                .map(AssetDisposal::getActualValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalEstimatedValue = allDisposals.stream()
                .map(AssetDisposal::getEstimatedValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> data = Map.of(
            "list", resultPage.getRecords(),
            "total", resultPage.getTotal(),
            "totalCount", totalCount,
            "totalActualValue", totalActualValue,
            "totalEstimatedValue", totalEstimatedValue,
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }

    @Override
    public Result<Map<String, Object>> getDepreciationReport(String startDate, String endDate, Long categoryId, Integer pageNum, Integer pageSize) {
        Page<AssetCard> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCard::getDeleted, 0);
        // 只查询在用的资产
        wrapper.in(AssetCard::getStatus, Arrays.asList(0, 1, 2));
        applyAssetDepartmentScope(wrapper);
        
        // 分类筛选
        if (categoryId != null) {
            wrapper.eq(AssetCard::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(AssetCard::getTotalAmount);
        
        Page<AssetCard> resultPage = assetCardMapper.selectPage(page, wrapper);
        
        // 计算折旧数据
        List<Map<String, Object>> depreciationList = new ArrayList<>();
        for (AssetCard asset : resultPage.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", asset.getId());
            item.put("assetCode", asset.getAssetCode());
            item.put("assetName", asset.getAssetName());
            item.put("categoryName", asset.getCategoryName());
            item.put("totalAmount", asset.getTotalAmount());
            
            // 简化折旧计算：平均年限法，假设残值率 5%
            BigDecimal originalValue = asset.getTotalAmount();
            if (originalValue != null) {
                BigDecimal salvageRate = new BigDecimal("0.05");
                BigDecimal depreciableAmount = originalValue.multiply(BigDecimal.ONE.subtract(salvageRate));
                
                // 假设使用年限为 5 年（60 个月）
                int useYears = 60;
                BigDecimal monthlyDepreciation = depreciableAmount.divide(new BigDecimal(useYears), 2, BigDecimal.ROUND_HALF_UP);
                
                // 计算已使用月数（简化：从创建时间到现在）
                LocalDateTime createTime = asset.getCreateTime();
                long monthsUsed = 0;
                if (createTime != null) {
                    monthsUsed = java.time.temporal.ChronoUnit.MONTHS.between(createTime, LocalDateTime.now());
                }
                
                BigDecimal accumulatedDepreciation = monthlyDepreciation.multiply(new BigDecimal(monthsUsed));
                if (accumulatedDepreciation.compareTo(originalValue) > 0) {
                    accumulatedDepreciation = originalValue.multiply(salvageRate);
                }
                
                BigDecimal netValue = originalValue.subtract(accumulatedDepreciation);
                
                item.put("monthlyDepreciation", monthlyDepreciation);
                item.put("accumulatedDepreciation", accumulatedDepreciation.setScale(2, BigDecimal.ROUND_HALF_UP));
                item.put("netValue", netValue.setScale(2, BigDecimal.ROUND_HALF_UP));
                item.put("useMonths", monthsUsed);
            }
            
            depreciationList.add(item);
        }
        
        // 统计折旧总额
        BigDecimal totalOriginalValue = depreciationList.stream()
                .map(i -> (BigDecimal)i.get("totalAmount"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalAccumulatedDepreciation = depreciationList.stream()
                .map(i -> (BigDecimal)i.get("accumulatedDepreciation"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalNetValue = depreciationList.stream()
                .map(i -> (BigDecimal)i.get("netValue"))
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> data = Map.of(
            "list", depreciationList,
            "total", resultPage.getTotal(),
            "totalOriginalValue", totalOriginalValue,
            "totalAccumulatedDepreciation", totalAccumulatedDepreciation,
            "totalNetValue", totalNetValue,
            "pageNum", pageNum,
            "pageSize", pageSize
        );
        
        return Result.success(data);
    }
    
    /**
     * 生成近 12 个月趋势数据
     */
    private List<Map<String, Object>> generateTrendData(List<AssetCard> allAssets) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            
            long count = allAssets.stream()
                    .filter(a -> a.getCreateTime() != null 
                            && (a.getCreateTime().isAfter(monthStart) || a.getCreateTime().isEqual(monthStart))
                            && (a.getCreateTime().isBefore(monthEnd) || a.getCreateTime().isEqual(monthEnd)))
                    .count();
            
            Map<String, Object> item = new HashMap<>();
            item.put("month", monthStart.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            item.put("count", count);
            trendData.add(item);
        }
        
        return trendData;
    }

    @Override
    public void exportStatisticsReport(String startDate, String endDate, Long departmentId, HttpServletResponse response) {
        try {
            // 查询数据
            LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssetCard::getDeleted, 0);

            Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(departmentId);
            if (dataPermissionService.shouldRestrictToOwnDepartment()) {
                if (scopedDepartmentId == null) {
                    wrapper.eq(AssetCard::getDepartmentId, -1L);
                } else {
                    wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
                }
            } else if (scopedDepartmentId != null) {
                wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
            }
            
            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                wrapper.ge(AssetCard::getCreateTime, startDateTime);
            }
            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
                wrapper.le(AssetCard::getCreateTime, endDateTime);
            }
            
            List<AssetCard> allAssets = assetCardMapper.selectList(wrapper);
            
            // 转换为导出格式
            List<StatisticsExportVO> exportData = new ArrayList<>();
            for (AssetCard asset : allAssets) {
                StatisticsExportVO vo = new StatisticsExportVO();
                vo.setAssetCode(asset.getAssetCode());
                vo.setAssetName(asset.getAssetName());
                vo.setCategoryName(asset.getCategoryName());
                vo.setDepartmentName(asset.getDepartmentName());
                vo.setLocationName(asset.getLocationName());
                vo.setStatus(getStatusText(asset.getStatus()));
                vo.setTotalAmount(asset.getTotalAmount());
                vo.setPurchaseDate(asset.getPurchaseDate() != null ? asset.getPurchaseDate().toString() : "");
                vo.setCreateTime(asset.getCreateTime() != null ? asset.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                exportData.add(vo);
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("资产统计报表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 导出 Excel
            EasyExcel.write(response.getOutputStream(), StatisticsExportVO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("资产统计")
                    .doWrite(exportData);
        } catch (Exception e) {
            throw new RuntimeException("导出统计报表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void exportDisposalReport(String startDate, String endDate, Integer disposalType, HttpServletResponse response) {
        try {
            // 查询数据
            LambdaQueryWrapper<AssetDisposal> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssetDisposal::getDeleted, 0);
            applyDisposalDepartmentScope(wrapper);
            
            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                wrapper.ge(AssetDisposal::getApplyTime, startDateTime);
            }
            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
                wrapper.le(AssetDisposal::getApplyTime, endDateTime);
            }
            
            if (disposalType != null) {
                wrapper.eq(AssetDisposal::getDisposalType, disposalType);
            }
            
            wrapper.orderByDesc(AssetDisposal::getApplyTime);
            List<AssetDisposal> allDisposals = assetDisposalMapper.selectList(wrapper);
            
            // 转换为导出格式
            List<DisposalExportVO> exportData = new ArrayList<>();
            for (AssetDisposal disposal : allDisposals) {
                DisposalExportVO vo = new DisposalExportVO();
                vo.setDisposalNo(disposal.getDisposalNo());
                vo.setAssetCode(disposal.getAssetCode());
                vo.setAssetName(disposal.getAssetName());
                vo.setDisposalType(getDisposalTypeText(disposal.getDisposalType()));
                vo.setEstimatedValue(disposal.getEstimatedValue());
                vo.setActualValue(disposal.getActualValue());
                vo.setDisposalStatus(getDisposalStatusText(disposal.getDisposalStatus()));
                vo.setApplyTime(disposal.getApplyTime() != null ? disposal.getApplyTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                vo.setApproveTime(disposal.getApproveTime() != null ? disposal.getApproveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
                exportData.add(vo);
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("资产处置报表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 导出 Excel
            EasyExcel.write(response.getOutputStream(), DisposalExportVO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("资产处置")
                    .doWrite(exportData);
        } catch (Exception e) {
            throw new RuntimeException("导出处置报表失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void exportDepreciationReport(String startDate, String endDate, Long categoryId, HttpServletResponse response) {
        try {
            // 查询数据
            LambdaQueryWrapper<AssetCard> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AssetCard::getDeleted, 0);
            wrapper.in(AssetCard::getStatus, Arrays.asList(0, 1, 2));
            applyAssetDepartmentScope(wrapper);
            
            if (categoryId != null) {
                wrapper.eq(AssetCard::getCategoryId, categoryId);
            }
            
            wrapper.orderByDesc(AssetCard::getTotalAmount);
            List<AssetCard> allAssets = assetCardMapper.selectList(wrapper);
            
            // 转换为导出格式
            List<DepreciationExportVO> exportData = new ArrayList<>();
            for (AssetCard asset : allAssets) {
                DepreciationExportVO vo = new DepreciationExportVO();
                vo.setAssetCode(asset.getAssetCode());
                vo.setAssetName(asset.getAssetName());
                vo.setCategoryName(asset.getCategoryName());
                vo.setOriginalValue(asset.getTotalAmount());
                
                // 计算折旧
                BigDecimal originalValue = asset.getTotalAmount();
                if (originalValue != null) {
                    BigDecimal salvageRate = new BigDecimal("0.05");
                    BigDecimal depreciableAmount = originalValue.multiply(BigDecimal.ONE.subtract(salvageRate));
                    int useYears = 60;
                    BigDecimal monthlyDepreciation = depreciableAmount.divide(new BigDecimal(useYears), 2, BigDecimal.ROUND_HALF_UP);
                    
                    LocalDateTime createTime = asset.getCreateTime();
                    long monthsUsed = 0;
                    if (createTime != null) {
                        monthsUsed = java.time.temporal.ChronoUnit.MONTHS.between(createTime, LocalDateTime.now());
                    }
                    
                    BigDecimal accumulatedDepreciation = monthlyDepreciation.multiply(new BigDecimal(monthsUsed));
                    if (accumulatedDepreciation.compareTo(originalValue) > 0) {
                        accumulatedDepreciation = originalValue.multiply(salvageRate);
                    }
                    
                    BigDecimal netValue = originalValue.subtract(accumulatedDepreciation);
                    
                    vo.setMonthlyDepreciation(monthlyDepreciation);
                    vo.setAccumulatedDepreciation(accumulatedDepreciation.setScale(2, BigDecimal.ROUND_HALF_UP));
                    vo.setNetValue(netValue.setScale(2, BigDecimal.ROUND_HALF_UP));
                    vo.setUseMonths((int) monthsUsed);
                }
                
                exportData.add(vo);
            }
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("资产折旧报表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            
            // 导出 Excel
            EasyExcel.write(response.getOutputStream(), DepreciationExportVO.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("资产折旧")
                    .doWrite(exportData);
        } catch (Exception e) {
            throw new RuntimeException("导出折旧报表失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "闲置";
            case 1 -> "在用";
            case 2 -> "维修中";
            case 3 -> "待处置";
            case 4 -> "已处置";
            case 5 -> "丢失";
            case 6 -> "借出";
            default -> "未知";
        };
    }

    /**
     * 获取处置类型文本
     */
    private String getDisposalTypeText(Integer type) {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "报废";
            case 2 -> "出售";
            case 3 -> "捐赠";
            case 4 -> "调拨";
            default -> "其他";
        };
    }

    /**
     * 获取处置状态文本
     */
    private String getDisposalStatusText(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待审批";
            case 1 -> "审批中";
            case 2 -> "已完成";
            case 3 -> "已驳回";
            default -> "未知";
        };
    }

    private void applyAssetDepartmentScope(LambdaQueryWrapper<AssetCard> wrapper) {
        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(null);
        if (dataPermissionService.shouldRestrictToOwnDepartment()) {
            if (scopedDepartmentId == null) {
                wrapper.eq(AssetCard::getDepartmentId, -1L);
                return;
            }
            wrapper.eq(AssetCard::getDepartmentId, scopedDepartmentId);
        }
    }

    private void applyDisposalDepartmentScope(LambdaQueryWrapper<AssetDisposal> wrapper) {
        Long scopedDepartmentId = dataPermissionService.resolveDepartmentScope(null);
        if (dataPermissionService.shouldRestrictToOwnDepartment()) {
            if (scopedDepartmentId == null) {
                wrapper.eq(AssetDisposal::getDepartmentId, -1L);
                return;
            }
            wrapper.eq(AssetDisposal::getDepartmentId, scopedDepartmentId);
        }
    }

    /**
     * 统计报表导出 VO
     */
    @lombok.Data
    public static class StatisticsExportVO implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产编号", index = 0)
        private String assetCode;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产名称", index = 1)
        private String assetName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "分类", index = 2)
        private String categoryName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "使用部门", index = 3)
        private String departmentName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "存放位置", index = 4)
        private String locationName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "状态", index = 5)
        private String status;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "金额", index = 6)
        private BigDecimal totalAmount;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "购置日期", index = 7)
        private String purchaseDate;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "创建时间", index = 8)
        private String createTime;
    }

    /**
     * 处置报表导出 VO
     */
    @lombok.Data
    public static class DisposalExportVO implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "处置单号", index = 0)
        private String disposalNo;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产编号", index = 1)
        private String assetCode;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产名称", index = 2)
        private String assetName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "处置类型", index = 3)
        private String disposalType;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "评估价值", index = 4)
        private BigDecimal estimatedValue;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "成交金额", index = 5)
        private BigDecimal actualValue;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "处置状态", index = 6)
        private String disposalStatus;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "申请时间", index = 7)
        private String applyTime;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "审批时间", index = 8)
        private String approveTime;
    }

    /**
     * 折旧报表导出 VO
     */
    @lombok.Data
    public static class DepreciationExportVO implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产编号", index = 0)
        private String assetCode;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "资产名称", index = 1)
        private String assetName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "分类", index = 2)
        private String categoryName;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "原值", index = 3)
        private BigDecimal originalValue;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "月折旧额", index = 4)
        private BigDecimal monthlyDepreciation;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "累计折旧", index = 5)
        private BigDecimal accumulatedDepreciation;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "净值", index = 6)
        private BigDecimal netValue;
        
        @com.alibaba.excel.annotation.ExcelProperty(value = "已使用月数", index = 7)
        private Integer useMonths;
    }
}
