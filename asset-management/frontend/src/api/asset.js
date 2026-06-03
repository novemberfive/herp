import request from './request'

// 获取资产列表
export function getAssetList(pageNum = 1, pageSize = 10, status = null) {
  return request({
    url: '/assets/list',
    method: 'get',
    params: { pageNum, pageSize, status }
  })
}

// 获取我的资产列表
export function getMyAssets(pageNum = 1, pageSize = 10) {
  return request({
    url: '/assets/my/list',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

// 获取部门资产列表
export function getDeptAssets(departmentId, pageNum = 1, pageSize = 10) {
  return request({
    url: '/assets/dept/list',
    method: 'get',
    params: { departmentId, pageNum, pageSize }
  })
}

// 获取资产详情
export function getAssetById(id) {
  return request({
    url: `/assets/${id}`
  })
}

// 创建资产
export function createAsset(data) {
  return request({
    url: '/assets',
    method: 'post',
    data
  })
}

// 更新资产
export function updateAsset(data) {
  return request({
    url: '/assets',
    method: 'put',
    data
  })
}

// 删除资产
export function deleteAsset(id) {
  return request({
    url: `/assets/${id}`,
    method: 'delete'
  })
}

// 获取资产统计
export function getStats() {
  return request({
    url: '/assets/stats'
  })
}
