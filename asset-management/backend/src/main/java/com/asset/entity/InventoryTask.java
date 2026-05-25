package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 盘点任务实体类
 */
@Data
@TableName("inventory_task")
public class InventoryTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号
     */
    @TableField("task_no")
    private String taskNo;

    /**
     * 任务名称
     */
    @TableField("task_name")
    private String taskName;

    /**
     * 类型：1-全盘，2-抽盘，3-循环盘点
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 盘点分类 IDs（逗号分隔）
     */
    @TableField("category_ids")
    private String categoryIds;

    /**
     * 盘点位置 IDs（逗号分隔）
     */
    @TableField("location_ids")
    private String locationIds;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 状态：0-未开始，1-进行中，2-已完成，3-已终止
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建人 ID
     */
    @TableField("create_user_id")
    private Long createUserId;

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
