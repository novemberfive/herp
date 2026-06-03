import request from './request'

// ==================== 采购申请相关接口 ====================

// 获取采购申请列表
export function getPurchaseList(pageNum = 1, pageSize = 10, query = {}) {
  return request({
    url: '/acquisition/purchase/list',
    method: 'get',
    params: { pageNum, pageSize, ...query }
  })
}

// 获取采购申请详情
export function getPurchaseById(id) {
  return request({
    url: `/acquisition/purchase/${id}`
  })
}

// 创建采购申请
export function createPurchase(data) {
  return request({
    url: '/acquisition/purchase',
    method: 'post',
    data
  })
}

// 更新采购申请
export function updatePurchase(data) {
  return request({
    url: '/acquisition/purchase',
    method: 'put',
    data
  })
}

// 删除采购申请
export function deletePurchase(id) {
  return request({
    url: `/acquisition/purchase/${id}`,
    method: 'delete'
  })
}

// 提交采购申请
export function submitPurchase(id) {
  return request({
    url: `/acquisition/purchase/${id}/submit`,
    method: 'post'
  })
}

// 审批通过采购申请
export function approvePurchase(id, opinion) {
  return request({
    url: `/acquisition/purchase/${id}/approve`,
    method: 'post',
    params: { opinion }
  })
}

// 审批拒绝采购申请
export function rejectPurchase(id, opinion) {
  return request({
    url: `/acquisition/purchase/${id}/reject`,
    method: 'post',
    params: { opinion }
  })
}

// 获取资产分类列表
export function getCategoryList() {
  return request({
    url: '/basic/category/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 100 }
  })
}

// 获取部门列表
export function getDepartmentList() {
  return request({
    url: '/basic/department/list',
    method: 'get',
    params: { pageNum: 1, pageSize: 100 }
  })
}
