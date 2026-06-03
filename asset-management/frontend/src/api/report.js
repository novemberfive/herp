import request from './request'

// 获取资产统计报表
export function getStatisticsReport(params) {
  return request({
    url: '/reports/statistics',
    method: 'get',
    params
  })
}

// 获取资产处置报表
export function getDisposalReport(params) {
  return request({
    url: '/reports/disposal',
    method: 'get',
    params
  })
}

// 获取资产折旧报表
export function getDepreciationReport(params) {
  return request({
    url: '/reports/depreciation',
    method: 'get',
    params
  })
}

// 导出资产统计报表
export function exportStatisticsReport(params) {
  return request({
    url: '/reports/export/statistics',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导出资产处置报表
export function exportDisposalReport(params) {
  return request({
    url: '/reports/export/disposal',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导出资产折旧报表
export function exportDepreciationReport(params) {
  return request({
    url: '/reports/export/depreciation',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
