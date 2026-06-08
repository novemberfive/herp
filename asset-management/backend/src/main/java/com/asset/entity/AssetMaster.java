package com.asset.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资产主数据实体类
 */
@Data
@TableName("asset_master")
public class AssetMaster implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("asset_code")
    private String assetCode;

    @TableField("asset_name")
    private String assetName;

    @TableField("category_id")
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;

    @TableField("specification")
    private String specification;

    @TableField("brand")
    private String brand;

    @TableField("unit")
    private String unit;

    @TableField("default_price")
    private BigDecimal defaultPrice;

    @TableField("warranty_months")
    private Integer warrantyMonths;

    @TableField("maintenance_cycle_days")
    private Integer maintenanceCycleDays;

    @TableField("use_life_months")
    private Integer useLifeMonths;

    @TableField("stock_quantity")
    private Integer stockQuantity;

    @TableField("min_stock")
    private Integer minStock;

    @TableField("max_stock")
    private Integer maxStock;

    @TableField("status")
    private Integer status;

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
