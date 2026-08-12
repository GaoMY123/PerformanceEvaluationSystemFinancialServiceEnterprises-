import request from '@/utils/request'

// 登录
export function loginApi(data) {
  return request.post('/auth/login', data)
}

// 注册
export function registerApi(data) {
  return request.post('/auth/register', data)
}

// 获取当前用户信息
export function getCurrentUserApi() {
  return request.get('/auth/info')
}

// 修改密码
export function changePasswordApi(data) {
  return request.put('/auth/password', data)
}

// 更新个人信息
export function updateProfileApi(data) {
  return request.put('/auth/profile', data)
}

// 密码找回
export function findPasswordApi(params) {
  return request.post('/auth/findPassword', null, { params })
}
