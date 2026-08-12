import request from '@/utils/request'

// 分页查询操作日志
export function getLogListApi(params) {
  return request.get('/log/page', { params })
}
