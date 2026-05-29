package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产借用登记实体类
 */
@Data
@TableName("asset_borrow")
public class AssetBorrow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 借用单号
     */
    @TableField("borrow_no")
    private String borrowNo;

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
     * 借用人 ID
     */
    @TableField("borrower_id")
    private Long borrowerId;

    /**
     * 借用人姓名
     */
    @TableField("borrower_name")
    private String borrowerName;

    /**
     * 借用人部门 ID
     */
    @TableField("borrower_department_id")
    private Long borrowerDepartmentId;

    /**
     * 借用人部门名称
     */
    @TableField("borrower_department_name")
    private String borrowerDepartmentName;

    /**
     * 借用人联系电话
     */
    @TableField("borrower_phone")
    private String borrowerPhone;

    /**
     * 借用用途
     */
    @TableField("borrow_purpose")
    private String borrowPurpose;

    /**
     * 借用日期
     */
    @TableField("borrow_date")
    private LocalDate borrowDate;

    /**
     * 预计归还日期
     */
    @TableField("expected_return_date")
    private LocalDate expectedReturnDate;

    /**
     * 实际归还日期
     */
    @TableField("actual_return_date")
    private LocalDate actualReturnDate;

    /**
     * 借用状态：0-待审批，1-借用中，2-已归还，3-已逾期，4-已取消
     */
    @TableField("borrow_status")
    private Integer borrowStatus;

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
     * 归还时资产状况
     */
    @TableField("return_condition")
    private String returnCondition;

    /**
     * 归还备注
     */
    @TableField("return_remark")
    private String returnRemark;

    /**
     * 是否已发送提醒：0-否，1-是
     */
    @TableField("reminder_sent")
    private Integer reminderSent;

    /**
     * 最后提醒时间
     */
    @TableField("last_reminder_time")
    private LocalDateTime lastReminderTime;

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
