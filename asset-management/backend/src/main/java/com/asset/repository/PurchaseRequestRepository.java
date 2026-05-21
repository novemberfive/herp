package com.asset.repository;

import com.asset.entity.PurchaseRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购申请数据访问层
 */
@Mapper
public interface PurchaseRequestRepository extends BaseMapper<PurchaseRequest> {
}
