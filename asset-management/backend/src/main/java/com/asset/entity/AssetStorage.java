package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产入库实体类
 */
@Data
@TableName("asset_storage")
public class AssetStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 入库单号
     */
    @TableField("storage_no")
    private String storageNo;

    /**
     * 关联验收 ID
     */
    @TableField("acceptance_id")
    private Long acceptanceId;

    /**
     * 验收单号
     */
    @TableField("acceptance_no")
    private String acceptanceNo;

    /**
     * 关联采购申请 ID
     */
    @TableField("purchase_request_id")
    private Long purchaseRequestId;

    /**
     * 采购申请单号
     */
    @TableField("purchase_request_no")
    private String purchaseRequestNo;

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
     * 入库数量
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
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

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
     * 保管人 ID
     */
    @TableField("keeper_id")
    private Long keeperId;

    /**
     * 保管人姓名
     */
    @TableField("keeper_name")
    private String keeperName;

    /**
     * 入库日期
     */
    @TableField("storage_date")
    private LocalDateTime storageDate;

    /**
     * 入库类型：1-采购入库，2-退库入库，3-调拨入库，4-其他入库
     */
    @TableField("storage_type")
    private Integer storageType;

    /**
     * 状态：0-待入库，1-已入库
     */
    @TableField("status")
    private Integer status;

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
