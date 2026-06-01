import request from './request'

// ==================== 附件管理 ====================

// 获取附件列表（分页）
export function getAttachmentList(params) {
  return request({
    url: '/attachments/list',
    method: 'get',
    params
  })
}

// 根据 ID 查询附件详情
export function getAttachmentById(id) {
  return request({
    url: `/attachments/${id}`,
    method: 'get'
  })
}

// 上传附件
export function uploadAttachment(data) {
  return request({
    url: '/attachments',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载附件
export function downloadAttachment(id) {
  return request({
    url: `/attachments/${id}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

// 删除附件
export function deleteAttachment(id) {
  return request({
    url: `/attachments/${id}`,
    method: 'delete'
  })
}

// 批量删除附件
export function batchDeleteAttachments(ids) {
  return request({
    url: '/attachments/batch',
    method: 'delete',
    data: ids
  })
}

// ==================== 变更记录管理 ====================

// 获取变更记录列表（分页）
export function getChangeList(params) {
  return request({
    url: '/changes/list',
    method: 'get',
    params
  })
}

// 根据 ID 查询变更详情
export function getChangeById(id) {
  return request({
    url: `/changes/${id}`,
    method: 'get'
  })
}

// 创建变更记录
export function createChange(data) {
  return request({
    url: '/changes',
    method: 'post',
    data
  })
}

// 删除变更记录
export function deleteChange(id) {
  return request({
    url: `/changes/${id}`,
    method: 'delete'
  })
}

// 批量删除变更记录
export function batchDeleteChanges(ids) {
  return request({
    url: '/changes/batch',
    method: 'delete',
    data: ids
  })
}

// 导出变更记录
export function exportChanges(params) {
  return request({
    url: '/changes/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
