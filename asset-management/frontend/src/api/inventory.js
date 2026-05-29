import request from './request'

// ==================== 盘点任务相关接口 ====================

// 获取盘点任务列表
export function getTaskList(pageNum = 1, pageSize = 10, status = null, taskType = null) {
  return request({
    url: '/inventory/task/list',
    method: 'get',
    params: { pageNum, pageSize, status, taskType }
  })
}

// 获取盘点任务详情
export function getTaskById(id) {
  return request({
    url: `/inventory/task/${id}`
  })
}

// 创建盘点任务
export function createTask(data) {
  return request({
    url: '/inventory/task',
    method: 'post',
    data
  })
}

// 更新盘点任务
export function updateTask(data) {
  return request({
    url: '/inventory/task',
    method: 'put',
    data
  })
}

// 删除盘点任务
export function deleteTask(id) {
  return request({
    url: `/inventory/task/${id}`,
    method: 'delete'
  })
}

// 开始盘点任务
export function startTask(id) {
  return request({
    url: `/inventory/task/${id}/start`,
    method: 'post'
  })
}

// 完成盘点任务
export function completeTask(id) {
  return request({
    url: `/inventory/task/${id}/complete`,
    method: 'post'
  })
}

// 终止盘点任务
export function cancelTask(id) {
  return request({
    url: `/inventory/task/${id}/cancel`,
    method: 'post'
  })
}

// ==================== 盘点计划相关接口 ====================

// 获取盘点计划列表
export function getPlanList(pageNum = 1, pageSize = 10, planName = null, planType = null, status = null) {
  return request({
    url: '/inventory/plan/list',
    method: 'get',
    params: { pageNum, pageSize, planName, planType, status }
  })
}

// 获取盘点计划详情
export function getPlanById(id) {
  return request({
    url: `/inventory/plan/${id}`
  })
}

// 创建盘点计划
export function createPlan(data) {
  return request({
    url: '/inventory/plan',
    method: 'post',
    data
  })
}

// 更新盘点计划
export function updatePlan(data) {
  return request({
    url: '/inventory/plan',
    method: 'put',
    data
  })
}

// 删除盘点计划
export function deletePlan(id) {
  return request({
    url: `/inventory/plan/${id}`,
    method: 'delete'
  })
}

// 执行盘点计划
export function executePlan(id) {
  return request({
    url: `/inventory/plan/${id}/execute`,
    method: 'post'
  })
}

// 启用盘点计划
export function enablePlan(id) {
  return request({
    url: `/inventory/plan/${id}/enable`,
    method: 'post'
  })
}

// 停用盘点计划
export function disablePlan(id) {
  return request({
    url: `/inventory/plan/${id}/disable`,
    method: 'post'
  })
}

// ==================== 盘点结果相关接口 ====================

// 获取盘点结果列表
export function getResultList(pageNum = 1, pageSize = 10, taskId = null, status = null) {
  return request({
    url: '/inventory/result/list',
    method: 'get',
    params: { pageNum, pageSize, taskId, status }
  })
}

// 获取盘点结果详情
export function getResultById(id) {
  return request({
    url: `/inventory/result/${id}`
  })
}

// 创建盘点结果
export function createResult(data) {
  return request({
    url: '/inventory/result',
    method: 'post',
    data
  })
}

// 更新盘点结果
export function updateResult(data) {
  return request({
    url: '/inventory/result',
    method: 'put',
    data
  })
}

// 删除盘点结果
export function deleteResult(id) {
  return request({
    url: `/inventory/result/${id}`,
    method: 'delete'
  })
}

// 提交盘点结果
export function submitResult(id) {
  return request({
    url: `/inventory/result/${id}/submit`,
    method: 'post'
  })
}

// 复核盘点结果
export function reviewResult(id) {
  return request({
    url: `/inventory/result/${id}/review`,
    method: 'post'
  })
}

// 处理盘点结果
export function handleResult(id) {
  return request({
    url: `/inventory/result/${id}/handle`,
    method: 'post'
  })
}

// 批量导入盘点结果
export function importResult(data) {
  return request({
    url: '/inventory/result/import',
    method: 'post',
    data
  })
}
