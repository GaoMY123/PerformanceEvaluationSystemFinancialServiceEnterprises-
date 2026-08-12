import request from '@/utils/request'

// 分页查询用户
export function getUserListApi(params) {
  return request.get('/user/page', { params })
}

// 获取用户详情
export function getUserApi(id) {
  return request.get(`/user/${id}`)
}

// 新增用户
export function addUserApi(data) {
  return request.post('/user', data)
}

// 更新用户
export function updateUserApi(data) {
  return request.put('/user', data)
}

// 重置密码（支持自定义密码）
export function resetPasswordApi(id, password) {
  return request.put(`/user/resetPassword/${id}`, { password })
}

// 修改用户状态
export function changeUserStatusApi(id, status) {
  return request.put(`/user/status/${id}/${status}`)
}

// 删除用户
export function deleteUserApi(id) {
  return request.delete(`/user/${id}`)
}

// 上传头像
export function uploadAvatarApi(formData) {
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
