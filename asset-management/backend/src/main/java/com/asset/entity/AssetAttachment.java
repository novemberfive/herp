package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资产附件实体类
 */
@Data
@TableName("asset_attachment")
public class AssetAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("asset_id")
    private Long assetId;

    @TableField("asset_code")
    private String assetCode;

    @TableField("asset_name")
    private String assetName;

    @TableField("attachment_name")
    private String attachmentName;

    @TableField("attachment_type")
    private Integer attachmentType;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_size")
    private Long fileSize;

    @TableField("upload_user")
    private String uploadUser;

    @TableField("upload_time")
    private LocalDateTime uploadTime;

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
