import request from '@/utils/request'

// 分页查询考核方案
export function getPlanListApi(params) {
  return request.get('/plan/page', { params })
}

// 获取方案详情
export function getPlanApi(id) {
  return request.get(`/plan/${id}`)
}

// 新增方案
export function addPlanApi(data) {
  return request.post('/plan', data)
}

// 更新方案
export function updatePlanApi(data) {
  return request.put('/plan', data)
}

// 删除方案
export function deletePlanApi(id) {
  return request.delete(`/plan/${id}`)
}

// 启动方案
export function startPlanApi(id) {
  return request.post(`/plan/start/${id}`)
}

// 完成方案
export function completePlanApi(id) {
  return request.post(`/plan/complete/${id}`)
}

// 获取方案评分规则
export function getPlanRulesApi(planId) {
  return request.get(`/plan/rule/${planId}`)
}

// 新增评分规则
export function addPlanRuleApi(data) {
  return request.post('/plan/rule', data)
}

// 更新评分规则
export function updatePlanRuleApi(data) {
  return request.put('/plan/rule', data)
}

// 删除评分规则
export function deletePlanRuleApi(id) {
  return request.delete(`/plan/rule/${id}`)
}

// 获取方案进度统计
export function getPlanProgressApi(id) {
  return request.get(`/plan/progress/${id}`)
}
