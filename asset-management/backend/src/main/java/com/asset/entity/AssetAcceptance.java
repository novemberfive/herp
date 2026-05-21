package com.asset.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 验收登记实体类
 */
@Data
@TableName("asset_acceptance")
public class AssetAcceptance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 验收数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 合格数量
     */
    @TableField("qualified_quantity")
    private Integer qualifiedQuantity;

    /**
     * 不合格数量
     */
    @TableField("unqualified_quantity")
    private Integer unqualifiedQuantity;

    /**
     * 实际单价
     */
    @TableField("actual_price")
    private BigDecimal actualPrice;

    /**
     * 实际总价
     */
    @TableField("actual_total")
    private BigDecimal actualTotal;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 供应商联系人
     */
    @TableField("supplier_contact")
    private String supplierContact;

    /**
     * 供应商电话
     */
    @TableField("supplier_phone")
    private String supplierPhone;

    /**
     * 到货日期
     */
    @TableField("delivery_date")
    private LocalDateTime deliveryDate;

    /**
     * 验收日期
     */
    @TableField("acceptance_date")
    private LocalDateTime acceptanceDate;

    /**
     * 验收人 ID
     */
    @TableField("acceptor_id")
    private Long acceptorId;

    /**
     * 验收人姓名
     */
    @TableField("acceptor_name")
    private String acceptorName;

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
     * 验收结果：0-待验收，1-验收合格，2-验收不合格，3-部分合格
     */
    @TableField("acceptance_result")
    private Integer acceptanceResult;

    /**
     * 不合格原因
     */
    @TableField("unqualified_reason")
    private String unqualifiedReason;

    /**
     * 处理意见
     */
    @TableField("handling_opinion")
    private String handlingOpinion;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 入库状态：0-未入库，1-已入库
     */
    @TableField("storage_status")
    private Integer storageStatus;

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
