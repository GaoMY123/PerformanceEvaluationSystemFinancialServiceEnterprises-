import request from '@/utils/request'

// 分页查询个人发展计划（管理视角）
export function getIdpListApi(params) {
  return request.get('/idp/page', { params })
}

// 获取我的发展计划
export function getMyIdpApi(params) {
  return request.get('/idp/my', { params })
}

// 获取部门发展计划（MANAGER视角）
export function getDepartmentIdpApi(params) {
  return request.get('/idp/department', { params })
}

// 获取IDP详情
export function getIdpApi(id) {
  return request.get(`/idp/${id}`)
}

// 根据考核方案自动生成发展计划
export function generateIdpApi(planId) {
  return request.post(`/idp/generate/${planId}`)
}

// 新增IDP
export function addIdpApi(data) {
  return request.post('/idp', data)
}

// 更新IDP
export function updateIdpApi(data) {
  return request.put('/idp', data)
}

// 修改IDP状态
export function changeIdpStatusApi(id, status) {
  return request.put(`/idp/status/${id}/${status}`)
}
