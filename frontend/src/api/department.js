import request from '@/utils/request'

// 获取部门列表（分页）
export function getDepartmentListApi(params) {
  return request.get('/department/page', { params })
}

// 获取所有部门（不分页，用于下拉选择）
export function getAllDepartmentsApi() {
  return request.get('/department/list')
}

// 获取部门树
export function getDepartmentTreeApi() {
  return request.get('/department/tree')
}

// 新增部门
export function addDepartmentApi(data) {
  return request.post('/department', data)
}

// 更新部门
export function updateDepartmentApi(data) {
  return request.put('/department', data)
}

// 删除部门
export function deleteDepartmentApi(id) {
  return request.delete(`/department/${id}`)
}
