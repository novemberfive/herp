package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 盘点结果实体类
 */
@Data
@TableName("inventory_result")
public class InventoryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 盘点任务 ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 任务编号
     */
    @TableField("task_no")
    private String taskNo;

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
     * 账面数量
     */
    @TableField("book_quantity")
    private Integer bookQuantity;

    /**
     * 实盘数量
     */
    @TableField("actual_quantity")
    private Integer actualQuantity;

    /**
     * 差异数量（正数为盘盈，负数为盘亏）
     */
    @TableField("difference_quantity")
    private Integer differenceQuantity;

    /**
     * 结果类型：0-正常，1-盘盈，2-盘亏，3-位置不符，4-信息不符
     */
    @TableField("result_type")
    private Integer resultType;

    /**
     * 差异原因
     */
    @TableField("discrepancy_reason")
    private String discrepancyReason;

    /**
     * 处理建议
     */
    @TableField("handling_suggestion")
    private String handlingSuggestion;

    /**
     * 盘点人 ID
     */
    @TableField("inventory_user_id")
    private Long inventoryUserId;

    /**
     * 盘点人姓名
     */
    @TableField("inventory_user_name")
    private String inventoryUserName;

    /**
     * 盘点时间
     */
    @TableField("inventory_time")
    private LocalDateTime inventoryTime;

    /**
     * 复核人 ID
     */
    @TableField("reviewer_id")
    private Long reviewerId;

    /**
     * 复核人姓名
     */
    @TableField("reviewer_name")
    private String reviewerName;

    /**
     * 复核时间
     */
    @TableField("review_time")
    private LocalDateTime reviewTime;

    /**
     * 复核状态：0-待复核，1-已复核，2-复核不通过
     */
    @TableField("review_status")
    private Integer reviewStatus;

    /**
     * 复核意见
     */
    @TableField("review_remark")
    private String reviewRemark;

    /**
     * 处理状态：0-待处理，1-已处理，2-无需处理
     */
    @TableField("process_status")
    private Integer processStatus;

    /**
     * 处理时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;

    /**
     * 处理人 ID
     */
    @TableField("process_user_id")
    private Long processUserId;

    /**
     * 处理人姓名
     */
    @TableField("process_user_name")
    private String processUserName;

    /**
     * 处理备注
     */
    @TableField("process_remark")
    private String processRemark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

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
