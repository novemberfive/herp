import request from './request'

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
