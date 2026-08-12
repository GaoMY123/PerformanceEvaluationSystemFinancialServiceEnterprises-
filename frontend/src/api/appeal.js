import request from '@/utils/request'

// 分页查询申诉（管理视角）
export function getAppealListApi(params) {
  return request.get('/appeal/page', { params })
}

// 获取我的申诉
export function getMyAppealsApi(params) {
  return request.get('/appeal/my', { params })
}

// 获取部门申诉（MANAGER视角）
export function getDepartmentAppealsApi(params) {
  return request.get('/appeal/department', { params })
}

// 获取申诉详情
export function getAppealApi(id) {
  return request.get(`/appeal/${id}`)
}

// 提交申诉
export function addAppealApi(data) {
  return request.post('/appeal', data)
}

// 处理申诉
export function handleAppealApi(id, data) {
  return request.put(`/appeal/handle/${id}`, data)
}
