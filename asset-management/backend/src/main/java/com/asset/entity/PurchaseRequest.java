package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购申请实体类
 */
@Data
@TableName("purchase_request")
public class PurchaseRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请单号
     */
    @TableField("request_no")
    private String requestNo;

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
     * 申请数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 预估单价
     */
    @TableField("estimated_price")
    private BigDecimal estimatedPrice;

    /**
     * 预估总价
     */
    @TableField("estimated_total")
    private BigDecimal estimatedTotal;

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
     * 申请日期
     */
    @TableField("apply_date")
    private LocalDateTime applyDate;

    /**
     * 期望到货日期
     */
    @TableField("expected_delivery_date")
    private LocalDateTime expectedDeliveryDate;

    /**
     * 采购原因
     */
    @TableField("purchase_reason")
    private String purchaseReason;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 审批状态：0-草稿，1-待审批，2-审批通过，3-审批拒绝，4-已采购
     */
    @TableField("status")
    private Integer status;

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
     * 审批意见
     */
    @TableField("approve_opinion")
    private String approveOpinion;

    /**
     * 创建人 ID
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人 ID
     */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
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
