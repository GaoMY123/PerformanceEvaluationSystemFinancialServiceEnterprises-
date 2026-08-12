import request from '@/utils/request'

// 分页查询绩效目标
export function getGoalListApi(params) {
  return request.get('/goal/page', { params })
}

// 获取目标树
export function getGoalTreeApi(year) {
  return request.get('/goal/tree', { params: { year } })
}

// 获取目标详情
export function getGoalApi(id) {
  return request.get(`/goal/${id}`)
}

// 新增目标
export function addGoalApi(data) {
  return request.post('/goal', data)
}

// 更新目标
export function updateGoalApi(data) {
  return request.put('/goal', data)
}

// 删除目标
export function deleteGoalApi(id) {
  return request.delete(`/goal/${id}`)
}

// 目标关联KPI
export function bindGoalKpiApi(data) {
  return request.post('/goal/bindKpi', data)
}

// 查询目标已关联的KPI
export function getGoalKpiListApi(goalId) {
  return request.get(`/goal/kpis/${goalId}`)
}

// 取消关联KPI
export function unbindGoalKpiApi(id) {
  return request.delete(`/goal/unbindKpi/${id}`)
}
