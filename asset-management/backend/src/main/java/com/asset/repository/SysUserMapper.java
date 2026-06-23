package com.asset.repository;

import com.asset.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询所有用户及部门名称
     */
    @Select("""
        SELECT u.*, d.dept_name AS department_name
        FROM sys_user u
        LEFT JOIN sys_department d ON u.department_id = d.id AND d.deleted = 0
        WHERE u.deleted = 0
        ORDER BY u.status DESC, u.create_time DESC
        """)
    List<SysUser> selectAllWithDepartment();

    /**
     * 查询启用中的用户及部门名称
     */
    @Select("""
        SELECT u.*, d.dept_name AS department_name
        FROM sys_user u
        LEFT JOIN sys_department d ON u.department_id = d.id AND d.deleted = 0
        WHERE u.deleted = 0 AND u.status = 1
        ORDER BY u.real_name, u.username
        """)
    List<SysUser> selectEnabledWithDepartment();

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    SysUser selectByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    SysUser selectByPhone(String phone);

    /**
     * 统计使用指定角色的用户数
     */
    @Select("SELECT COUNT(1) FROM sys_user WHERE role = #{role} AND deleted = 0")
    long countByRole(String role);
}
