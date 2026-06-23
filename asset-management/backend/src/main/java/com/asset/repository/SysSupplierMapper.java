package com.asset.repository;

import com.asset.entity.SysSupplier;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统供应商 Mapper 接口
 */
@Mapper
public interface SysSupplierMapper extends BaseMapper<SysSupplier> {

    @Select("SELECT * FROM sys_supplier WHERE deleted = 0 ORDER BY sort_order, id")
    List<SysSupplier> selectAll();

    @Select("""
        SELECT * FROM sys_supplier
        WHERE deleted = 0
          AND status = 1
        ORDER BY sort_order, id
        """)
    List<SysSupplier> selectEnabled();

    @Select("""
        SELECT * FROM sys_supplier
        WHERE deleted = 0
          AND supplier_name = #{supplierName}
        LIMIT 1
        """)
    SysSupplier selectBySupplierName(@Param("supplierName") String supplierName);

    @Select("""
        SELECT * FROM sys_supplier
        WHERE deleted = 0
          AND supplier_code = #{supplierCode}
        LIMIT 1
        """)
    SysSupplier selectBySupplierCode(@Param("supplierCode") String supplierCode);
}
