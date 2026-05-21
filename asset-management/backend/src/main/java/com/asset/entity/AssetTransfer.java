package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产调拨实体类
 */
@Data
@TableName("asset_transfer")
public class AssetTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 调拨单号
     */
    @TableField("transfer_code")
    private String transferCode;

    /**
     * 资产 ID
     */
    @TableField("asset_id")
    private Long assetId;

    /**
     * 资产编码（冗余）
     */
    @TableField("asset_code")
    private String assetCode;

    /**
     * 资产名称（冗余）
     */
    @TableField("asset_name")
    private String assetName;

    /**
     * 调出部门 ID
     */
    @TableField("from_department_id")
    private Long fromDepartmentId;

    /**
     * 调出部门名称（冗余）
     */
    @TableField("from_department_name")
    private String fromDepartmentName;

    /**
     * 调入部门 ID
     */
    @TableField("to_department_id")
    private Long toDepartmentId;

    /**
     * 调入部门名称（冗余）
     */
    @TableField("to_department_name")
    private String toDepartmentName;

    /**
     * 调拨原因
     */
    @TableField("transfer_reason")
    private String transferReason;

    /**
     * 调拨类型：1-部门间调拨，2-人员变更，3-位置变更
     */
    @TableField("transfer_type")
    private Integer transferType;

    /**
     * 申请人 ID
     */
    @TableField("applicant_id")
    private Long applicantId;

    /**
     * 申请人姓名（冗余）
     */
    @TableField("applicant_name")
    private String applicantName;

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
     * 审批人姓名（冗余）
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
     * 调拨状态：0-待调拨，1-调拨中，2-已完成，3-已取消
     */
    @TableField("transfer_status")
    private Integer transferStatus;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
