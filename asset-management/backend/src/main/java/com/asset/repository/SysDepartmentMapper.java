package com.asset.repository;

import com.asset.entity.SysDepartment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统部门 Mapper 接口
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /**
     * 查询所有部门（按层级排序）
     */
    @Select("SELECT * FROM sys_department WHERE deleted = 0 ORDER BY level, sort_order")
    List<SysDepartment> selectAll();

    /**
     * 根据父级 ID 查询子部门
     */
    @Select("SELECT * FROM sys_department WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order")
    List<SysDepartment> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询一级部门
     */
    @Select("SELECT * FROM sys_department WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order")
    List<SysDepartment> selectTopLevel();
}
