package com.asset.repository;

import com.asset.entity.AssetCard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资产卡片 Mapper 接口
 */
@Mapper
public interface AssetCardMapper extends BaseMapper<AssetCard> {

    /**
     * 根据分类 ID 查询资产列表
     */
    @Select("SELECT * FROM asset_card WHERE category_id = #{categoryId} AND deleted = 0")
    List<AssetCard> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 根据状态查询资产列表
     */
    @Select("SELECT * FROM asset_card WHERE status = #{status} AND deleted = 0")
    List<AssetCard> selectByStatus(@Param("status") Integer status);

    /**
     * 统计各状态资产数量
     */
    @Select("SELECT status, COUNT(*) as count FROM asset_card WHERE deleted = 0 GROUP BY status")
    List<StatusCount> countByStatus();

    /**
     * 状态统计 DTO
     */
    class StatusCount {
        private Integer status;
        private Long count;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
