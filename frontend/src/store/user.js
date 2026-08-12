import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, getCurrentUserApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 从本地存储恢复
  const token = ref(sessionStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(sessionStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const realName = computed(() => userInfo.value?.realName || '')
  const userId = computed(() => userInfo.value?.id || null)

  // 登录
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    // 登录返回 { user, token }，分别提取
    token.value = res.data.token
    sessionStorage.setItem('token', res.data.token)
    // 直接从登录响应中保存用户信息
    userInfo.value = res.data.user
    sessionStorage.setItem('userInfo', JSON.stringify(res.data.user))
    return res
  }

  // 获取当前用户信息
  async function fetchUserInfo() {
    const res = await getCurrentUserApi()
    userInfo.value = res.data
    sessionStorage.setItem('userInfo', JSON.stringify(res.data))
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
  }

  return {
    token, userInfo, isLoggedIn, role, realName, userId,
    login, fetchUserInfo, logout
  }
})
