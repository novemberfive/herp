package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产维修记录实体类
 */
@Data
@TableName("asset_maintenance")
public class AssetMaintenance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 维修单号
     */
    @TableField("maintenance_no")
    private String maintenanceNo;

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
     * 使用部门 ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 使用部门名称
     */
    @TableField("department_name")
    private String departmentName;

    /**
     * 存放位置 ID
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 存放位置名称
     */
    @TableField("location_name")
    private String locationName;

    /**
     * 维修类型：1-日常维修，2-大修，3-保养，4-巡检
     */
    @TableField("maintenance_type")
    private Integer maintenanceType;

    /**
     * 故障描述
     */
    @TableField("fault_description")
    private String faultDescription;

    /**
     * 故障类型：0-其他，1-硬件故障，2-软件故障，3-人为损坏，4-自然老化
     */
    @TableField("fault_type")
    private Integer faultType;

    /**
     * 维修方式
     */
    @TableField("maintenance_method")
    private String maintenanceMethod;

    /**
     * 维修结果：0-待维修，1-维修中，2-维修完成，3-无法修复
     */
    @TableField("maintenance_result")
    private Integer maintenanceResult;

    /**
     * 维修费用
     */
    @TableField("maintenance_cost")
    private BigDecimal maintenanceCost;

    /**
     * 维修开始日期
     */
    @TableField("start_date")
    private LocalDateTime startDate;

    /**
     * 维修结束日期
     */
    @TableField("end_date")
    private LocalDateTime endDate;

    /**
     * 维修人 ID
     */
    @TableField("maintainer_id")
    private Long maintainerId;

    /**
     * 维修人姓名
     */
    @TableField("maintainer_name")
    private String maintainerName;

    /**
     * 维修单位（外部维修时填写）
     */
    @TableField("maintenance_unit")
    private String maintenanceUnit;

    /**
     * 联系人
     */
    @TableField("contact_person")
    private String contactPerson;

    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 是否保修：0-否，1-是
     */
    @TableField("warranty_claim")
    private Integer warrantyClaim;

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
