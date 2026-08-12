import request from '@/utils/request'

// 分页查询薪酬调整
export function getSalaryListApi(params) {
  return request.get('/salary/page', { params })
}

// 获取部门薪酬调整（MANAGER视角，只读）
export function getDepartmentSalaryApi(params) {
  return request.get('/salary/department', { params })
}

// 获取薪酬调整详情
export function getSalaryApi(id) {
  return request.get(`/salary/${id}`)
}

// 生成薪酬调整建议
export function generateSalaryApi(planId) {
  return request.post(`/salary/generate/${planId}`)
}

// 更新薪酬调整记录
export function updateSalaryApi(data) {
  return request.put('/salary', data)
}

// 审批薪酬调整
export function approveSalaryApi(id, status) {
  return request.put(`/salary/approve/${id}/${status}`)
}
