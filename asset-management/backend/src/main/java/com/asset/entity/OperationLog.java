package com.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作审计日志
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("module")
    private String module;

    @TableField("action")
    private String action;

    @TableField("biz_type")
    private String bizType;

    @TableField("biz_id")
    private String bizId;

    @TableField("description")
    private String description;

    @TableField("operator_id")
    private Long operatorId;

    @TableField("operator_username")
    private String operatorUsername;

    @TableField("operator_name")
    private String operatorName;

    @TableField("operator_role")
    private String operatorRole;

    @TableField("request_method")
    private String requestMethod;

    @TableField("request_uri")
    private String requestUri;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("status")
    private Integer status;

    @TableField("result_code")
    private Integer resultCode;

    @TableField("result_message")
    private String resultMessage;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
