import request from './request'

// ==================== 资产分类管理 ====================

// 获取所有分类列表
export function getCategoryList() {
  return request({
    url: '/categories/list',
    method: 'get'
  })
}

// 根据父级 ID 查询子分类
export function getCategoryByParentId(parentId) {
  return request({
    url: `/categories/parent/${parentId}`,
    method: 'get'
  })
}

// 查询一级分类
export function getTopLevelCategories() {
  return request({
    url: '/categories/top-level',
    method: 'get'
  })
}

// 创建分类
export function createCategory(data) {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(data) {
  return request({
    url: '/categories',
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}

// ==================== 存放位置管理 ====================

// 获取所有位置列表
export function getLocationList() {
  return request({
    url: '/locations/list',
    method: 'get'
  })
}

// 根据父级 ID 查询子位置
export function getLocationByParentId(parentId) {
  return request({
    url: `/locations/parent/${parentId}`,
    method: 'get'
  })
}

// 查询一级位置
export function getTopLevelLocations() {
  return request({
    url: '/locations/top-level',
    method: 'get'
  })
}

// 创建位置
export function createLocation(data) {
  return request({
    url: '/locations',
    method: 'post',
    data
  })
}

// 更新位置
export function updateLocation(data) {
  return request({
    url: '/locations',
    method: 'put',
    data
  })
}

// 删除位置
export function deleteLocation(id) {
  return request({
    url: `/locations/${id}`,
    method: 'delete'
  })
}
