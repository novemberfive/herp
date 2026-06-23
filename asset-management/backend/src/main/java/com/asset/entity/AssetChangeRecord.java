package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产变更记录实体类
 */
@Data
@TableName("asset_change_record")
public class AssetChangeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("asset_id")
    private Long assetId;

    @TableField("asset_code")
    private String assetCode;

    @TableField("asset_name")
    private String assetName;

    @TableField("change_type")
    private Integer changeType;

    @TableField("change_field")
    private String changeField;

    @TableField("before_value")
    private String beforeValue;

    @TableField("after_value")
    private String afterValue;

    @TableField("change_reason")
    private String changeReason;

    @TableField("operator_name")
    private String operatorName;

    @TableField("operate_time")
    private LocalDateTime operateTime;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
