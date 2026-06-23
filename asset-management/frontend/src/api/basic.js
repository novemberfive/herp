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

// ==================== 用户与角色管理 ====================

// 获取所有用户列表
export function getUserList() {
  return request({
    url: '/users/list',
    method: 'get'
  })
}

// 获取启用中的用户列表
export function getEnabledUserList() {
  return request({
    url: '/users/enabled',
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/users',
    method: 'put',
    data
  })
}

// 重置用户密码
export function resetUserPassword(id) {
  return request({
    url: `/users/${id}/reset-password`,
    method: 'put'
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 获取所有角色列表
export function getRoleList() {
  return request({
    url: '/roles/list',
    method: 'get'
  })
}

// 获取启用中的角色列表
export function getEnabledRoleList() {
  return request({
    url: '/roles/enabled',
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: '/roles',
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

// 获取操作审计日志分页列表
export function getOperationLogList(params) {
  return request({
    url: '/operation-logs/list',
    method: 'get',
    params
  })
}

// ==================== 部门管理 ====================

// 获取所有部门列表
export function getDepartmentList() {
  return request({
    url: '/departments/list',
    method: 'get'
  })
}

// 根据父级 ID 查询子部门
export function getDepartmentByParentId(parentId) {
  return request({
    url: `/departments/parent/${parentId}`,
    method: 'get'
  })
}

// 查询一级部门
export function getTopLevelDepartments() {
  return request({
    url: '/departments/top-level',
    method: 'get'
  })
}

// 创建部门
export function createDepartment(data) {
  return request({
    url: '/departments',
    method: 'post',
    data
  })
}

// 更新部门
export function updateDepartment(data) {
  return request({
    url: '/departments',
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDepartment(id) {
  return request({
    url: `/departments/${id}`,
    method: 'delete'
  })
}

// ==================== 供应商管理 ====================

// 获取所有供应商列表
export function getSupplierList() {
  return request({
    url: '/suppliers/list',
    method: 'get'
  })
}

// 获取启用中的供应商列表
export function getEnabledSupplierList() {
  return request({
    url: '/suppliers/enabled',
    method: 'get'
  })
}

// 创建供应商
export function createSupplier(data) {
  return request({
    url: '/suppliers',
    method: 'post',
    data
  })
}

// 更新供应商
export function updateSupplier(data) {
  return request({
    url: '/suppliers',
    method: 'put',
    data
  })
}

// 删除供应商
export function deleteSupplier(id) {
  return request({
    url: `/suppliers/${id}`,
    method: 'delete'
  })
}

// ==================== 资产主数据管理 ====================

// 获取资产主数据列表
export function getAssetMasterList() {
  return request({
    url: '/asset-masters/list',
    method: 'get'
  })
}

// 获取启用中的资产主数据列表
export function getEnabledAssetMasterList() {
  return request({
    url: '/asset-masters/enabled',
    method: 'get'
  })
}

// 创建资产主数据
export function createAssetMaster(data) {
  return request({
    url: '/asset-masters',
    method: 'post',
    data
  })
}

// 更新资产主数据
export function updateAssetMaster(data) {
  return request({
    url: '/asset-masters',
    method: 'put',
    data
  })
}

// 更新资产主数据状态
export function updateAssetMasterStatus(id, status) {
  return request({
    url: `/asset-masters/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 删除资产主数据
export function deleteAssetMaster(id) {
  return request({
    url: `/asset-masters/${id}`,
    method: 'delete'
  })
}

// ==================== 维保记录管理 ====================

// 获取维保记录列表（分页）
export function getMaintenanceList(params) {
  return request({
    url: '/maintenance/list',
    method: 'get',
    params
  })
}

// 根据 ID 查询维保详情
export function getMaintenanceById(id) {
  return request({
    url: `/maintenance/${id}`,
    method: 'get'
  })
}

// 创建维保申请
export function createMaintenance(data) {
  return request({
    url: '/maintenance',
    method: 'post',
    data
  })
}

// 更新维保信息
export function updateMaintenance(data) {
  return request({
    url: '/maintenance',
    method: 'put',
    data
  })
}

// 删除维保记录
export function deleteMaintenance(id) {
  return request({
    url: `/maintenance/${id}`,
    method: 'delete'
  })
}

// 审批维保申请
export function approveMaintenance(id, approveStatus, approveRemark, approverId) {
  return request({
    url: `/maintenance/${id}/approve`,
    method: 'post',
    params: { approveStatus, approveRemark, approverId }
  })
}

// 开始维修
export function startMaintenance(id, maintainerId, maintainerName) {
  return request({
    url: `/maintenance/${id}/start`,
    method: 'post',
    params: { maintainerId, maintainerName }
  })
}

// 完成维修
export function completeMaintenance(id, maintenanceResult, maintenanceMethod, maintenanceCost) {
  return request({
    url: `/maintenance/${id}/complete`,
    method: 'post',
    params: { maintenanceResult, maintenanceMethod, maintenanceCost }
  })
}

// 取消维修
export function cancelMaintenance(id) {
  return request({
    url: `/maintenance/${id}/cancel`,
    method: 'post'
  })
}
