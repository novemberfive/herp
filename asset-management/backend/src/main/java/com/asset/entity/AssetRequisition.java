package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产领用退库实体类
 */
@Data
@TableName("asset_requisition")
public class AssetRequisition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 领用单号
     */
    @TableField("requisition_no")
    private String requisitionNo;

    /**
     * 业务类型：1-领用，2-退库
     */
    @TableField("business_type")
    private Integer businessType;

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
     * 领用数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单价
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 原使用部门 ID
     */
    @TableField("original_department_id")
    private Long originalDepartmentId;

    /**
     * 原使用部门名称
     */
    @TableField("original_department_name")
    private String originalDepartmentName;

    /**
     * 原保管人 ID
     */
    @TableField("original_keeper_id")
    private Long originalKeeperId;

    /**
     * 原保管人姓名
     */
    @TableField("original_keeper_name")
    private String originalKeeperName;

    /**
     * 新使用部门 ID
     */
    @TableField("new_department_id")
    private Long newDepartmentId;

    /**
     * 新使用部门名称
     */
    @TableField("new_department_name")
    private String newDepartmentName;

    /**
     * 新保管人 ID
     */
    @TableField("new_keeper_id")
    private Long newKeeperId;

    /**
     * 新保管人姓名
     */
    @TableField("new_keeper_name")
    private String newKeeperName;

    /**
     * 领用/退库日期
     */
    @TableField("requisition_date")
    private LocalDateTime requisitionDate;

    /**
     * 预计归还日期（仅领用）
     */
    @TableField("expected_return_date")
    private LocalDateTime expectedReturnDate;

    /**
     * 实际归还日期（仅退库）
     */
    @TableField("actual_return_date")
    private LocalDateTime actualReturnDate;

    /**
     * 领用/退库原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 审批状态：0-草稿，1-待审批，2-审批通过，3-审批拒绝，4-已完成
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
