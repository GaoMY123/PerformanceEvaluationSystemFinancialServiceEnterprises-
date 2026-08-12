import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器 - 自动携带 Token，统一转换分页参数
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    // 前端统一用 page/size，后端要求 pageNum/pageSize
    if (config.params) {
      if (config.params.page !== undefined) {
        config.params.pageNum = config.params.page
        delete config.params.page
      }
      if (config.params.size !== undefined) {
        config.params.pageSize = config.params.size
        delete config.params.size
      }
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 - 统一处理错误
service.interceptors.response.use(
  response => {
    const res = response.data
    // 如果是文件下载（blob），直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        const userStore = useUserStore()
        // 公开页面（注册、找回密码）不弹提示不跳转
        const publicPaths = ['/auth/register', '/auth/findPassword', '/department/list']
        const isPublic = publicPaths.some(p => error.config.url?.includes(p))
        if (!isPublic) {
          ElMessage.error('登录已过期，请重新登录')
          userStore.logout()
          router.push('/login')
        } else {
          // 公开页面带过期token时，清除token后静默重试（不带token）
          userStore.logout()
          const config = error.config
          delete config.headers['Authorization']
          return service.request(config)
        }
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else {
        ElMessage.error(error.response.data?.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

export default service
