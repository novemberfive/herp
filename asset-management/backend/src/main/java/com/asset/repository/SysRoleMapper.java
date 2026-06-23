package com.asset.repository;

import com.asset.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper 接口
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
        SELECT *
        FROM sys_role
        WHERE deleted = 0
        ORDER BY sort_order ASC, create_time ASC
        """)
    List<SysRole> selectAll();

    @Select("""
        SELECT *
        FROM sys_role
        WHERE deleted = 0 AND status = 1
        ORDER BY sort_order ASC, create_time ASC
        """)
    List<SysRole> selectEnabled();

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    SysRole selectByRoleCode(String roleCode);

    @Select("SELECT * FROM sys_role WHERE role_name = #{roleName} AND deleted = 0")
    SysRole selectByRoleName(String roleName);
}
