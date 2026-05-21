package com.asset.enums;

/**
 * 资产状态枚举
 */
public enum AssetStatus {
    /**
     * 闲置 - 可用但未分配
     */
    IDLE(0, "闲置"),
    
    /**
     * 在用 - 正在使用中
     */
    IN_USE(1, "在用"),
    
    /**
     * 维修中 - 正在维修
     */
    REPAIRING(2, "维修中"),
    
    /**
     * 待处置 - 等待处置
     */
    PENDING_DISPOSAL(3, "待处置"),
    
    /**
     * 已处置 - 已完成处置
     */
    DISPOSED(4, "已处置"),
    
    /**
     * 丢失 - 资产丢失
     */
    LOST(5, "丢失"),
    
    /**
     * 借出 - 临时借出
     */
    BORROWED(6, "借出");

    private final int code;
    private final String desc;

    AssetStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AssetStatus fromCode(int code) {
        for (AssetStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown asset status code: " + code);
    }
}
