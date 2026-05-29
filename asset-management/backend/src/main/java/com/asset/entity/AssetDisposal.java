package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产处置申请实体类
 */
@Data
@TableName("asset_disposal")
public class AssetDisposal implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 处置单号
     */
    @TableField("disposal_no")
    private String disposalNo;

    /**
     * 资产 ID
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 资产编码
     */
    @TableField("asset_code")
    private String assetCode;

    /**
     * 资产名称
     */
    @TableField("asset_name")
    private String assetName;

    /**
     * 规格型号
     */
    @TableField("specification")
    private String specification;

    /**
     * 分类 ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 原值
     */
    @TableField("original_value")
    private BigDecimal originalValue;

    /**
     * 已提折旧
     */
    @TableField("accumulated_depreciation")
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值
     */
    @TableField("net_value")
    private BigDecimal netValue;

    /**
     * 购置日期
     */
    @TableField("purchase_date")
    private LocalDate purchaseDate;

    /**
     * 启用日期
     */
    @TableField("enable_date")
    private LocalDate enableDate;

    /**
     * 预计使用年限（月）
     */
    @TableField("expected_use_years")
    private Integer expectedUseYears;

    /**
     * 已使用年限（月）
     */
    @TableField("used_years")
    private Integer usedYears;

    /**
     * 处置类型：1-报废，2-出售，3-捐赠，4-调出
     */
    @TableField("disposal_type")
    private Integer disposalType;

    /**
     * 处置原因
     */
    @TableField("disposal_reason")
    private String disposalReason;

    /**
     * 处置方式：1-公开拍卖，2-协议转让，3-废品回收，4-其他
     */
    @TableField("disposal_method")
    private Integer disposalMethod;

    /**
     * 评估价值
     */
    @TableField("estimated_value")
    private BigDecimal estimatedValue;

    /**
     * 实际成交金额
     */
    @TableField("actual_value")
    private BigDecimal actualValue;

    /**
     * 受让方名称（出售时填写）
     */
    @TableField("buyer_name")
    private String buyerName;

    /**
     * 受让方联系人
     */
    @TableField("buyer_contact")
    private String buyerContact;

    /**
     * 申请人 ID
     */
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 申请人姓名
     */
    @TableField("applicant_name")
    private String applicantName;

    /**
     * 申请部门 ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 申请部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    private LocalDateTime applyTime;

    /**
     * 审批人 ID
     */
    @TableField("approver_id")
    private Long approverId;

    /**
     * 审批人姓名
     */
    @TableField("approver_name")
    private String approverName;

    /**
     * 审批时间
     */
    @TableField("approve_time")
    private LocalDateTime approveTime;

    /**
     * 审批状态：0-待审批，1-已通过，2-已拒绝
     */
    @TableField("approve_status")
    private Integer approveStatus;

    /**
     * 审批意见
     */
    @TableField("approve_remark")
    private String approveRemark;

    /**
     * 处置状态：0-待处置，1-处置中，2-已完成，3-已取消
     */
    @TableField("disposal_status")
    private Integer disposalStatus;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 附件 URLs（逗号分隔）
     */
    @TableField("attachment_urls")
    private String attachmentUrls;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人 ID
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    @TableField("update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
