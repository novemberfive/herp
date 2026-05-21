package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产卡片实体类
 */
@Data
@TableName("asset_card")
public class AssetCard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资产编码（唯一）
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
     * 分类名称（冗余）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 数量
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
     * 存放位置 ID
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 存放位置名称（冗余）
     */
    @TableField(exist = false)
    private String locationName;

    /**
     * 使用部门 ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 使用部门名称（冗余）
     */
    @TableField(exist = false)
    private String departmentName;

    /**
     * 使用人 ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 使用人姓名（冗余）
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 资产状态：0-闲置，1-在用，2-维修中，3-待处置，4-已处置，5-丢失，6-借出
     */
    @TableField("status")
    private Integer status;

    /**
     * 购置日期
     */
    @TableField("purchase_date")
    private LocalDateTime purchaseDate;

    /**
     * 启用日期
     */
    @TableField("enable_date")
    private LocalDateTime enableDate;

    /**
     * 预计使用年限（月）
     */
    @TableField("expected_use_years")
    private Integer expectedUseYears;

    /**
     * 折旧方法：1-平均年限法，2-工作量法，3-双倍余额递减法
     */
    @TableField("depreciation_method")
    private Integer depreciationMethod;

    /**
     * 已计提折旧金额
     */
    @TableField("accumulated_depreciation")
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值
     */
    @TableField("net_value")
    private BigDecimal netValue;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

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
     * 保修期限（月）
     */
    @TableField("warranty_period")
    private Integer warrantyPeriod;

    /**
     * 保修截止日期
     */
    @TableField("warranty_end_date")
    private LocalDateTime warrantyEndDate;

    /**
     * 图片路径（多张逗号分隔）
     */
    @TableField("images")
    private String images;

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
