import request from './request'

// 获取调拨列表
export function getTransferList(pageNum = 1, pageSize = 10, approveStatus = null, transferStatus = null) {
  return request({
    url: '/transfers/list',
    method: 'get',
    params: { pageNum, pageSize, approveStatus, transferStatus }
  })
}

// 获取调拨详情
export function getTransferById(id) {
  return request({
    url: `/transfers/${id}`
  })
}

// 创建调拨申请
export function createTransfer(data) {
  return request({
    url: '/transfers',
    method: 'post',
    data
  })
}

// 更新调拨信息
export function updateTransfer(data) {
  return request({
    url: '/transfers',
    method: 'put',
    data
  })
}

// 删除调拨记录
export function deleteTransfer(id) {
  return request({
    url: `/transfers/${id}`,
    method: 'delete'
  })
}

// 审批调拨申请
export function approveTransfer(id, approveStatus, approveRemark) {
  return request({
    url: `/transfers/${id}/approve`,
    method: 'post',
    params: { approveStatus, approveRemark }
  })
}

// 完成调拨
export function completeTransfer(id) {
  return request({
    url: `/transfers/${id}/complete`,
    method: 'post'
  })
}

// 取消调拨
export function cancelTransfer(id) {
  return request({
    url: `/transfers/${id}/cancel`,
    method: 'post'
  })
}
