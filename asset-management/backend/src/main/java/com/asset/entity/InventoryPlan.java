package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 盘点计划实体类
 */
@Data
@TableName("inventory_plan")
public class InventoryPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划编号
     */
    @TableField("plan_no")
    private String planNo;

    /**
     * 计划名称
     */
    @TableField("plan_name")
    private String planName;

    /**
     * 计划类型：1-定期盘点，2-临时盘点
     */
    @TableField("plan_type")
    private Integer planType;

    /**
     * 周期类型：1-每日，2-每周，3-每月，4-每季，5-每年（定期盘点时有效）
     */
    @TableField("cycle_type")
    private Integer cycleType;

    /**
     * 周期执行日（如每月第几天，每周第几天）
     */
    @TableField("cycle_day")
    private Integer cycleDay;

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
     * 计划开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 计划结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 状态：0-停用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建人 ID
     */
    @TableField("create_user_id")
    private Long createUserId;

    /**
     * 创建人姓名
     */
    @TableField("create_user_name")
    private String createUserName;

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
