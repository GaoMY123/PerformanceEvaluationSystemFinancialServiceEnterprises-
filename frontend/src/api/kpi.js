import request from '@/utils/request'

// 分页查询KPI指标
export function getKpiListApi(params) {
  return request.get('/kpi/page', { params })
}

// 获取所有KPI（下拉选择）
export function getAllKpiApi() {
  return request.get('/kpi/list')
}

// 获取KPI详情
export function getKpiApi(id) {
  return request.get(`/kpi/${id}`)
}

// 新增KPI
export function addKpiApi(data) {
  return request.post('/kpi', data)
}

// 更新KPI
export function updateKpiApi(data) {
  return request.put('/kpi', data)
}

// 删除KPI
export function deleteKpiApi(id) {
  return request.delete(`/kpi/${id}`)
}
