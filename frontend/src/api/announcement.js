import request from '@/utils/request'

// 分页查询公告
export function getAnnouncementListApi(params) {
  return request.get('/announcement/page', { params })
}

// 获取最新公告
export function getLatestAnnouncementsApi() {
  return request.get('/announcement/latest')
}

// 获取公告详情
export function getAnnouncementApi(id) {
  return request.get(`/announcement/${id}`)
}

// 新增公告
export function addAnnouncementApi(data) {
  return request.post('/announcement', data)
}

// 更新公告
export function updateAnnouncementApi(data) {
  return request.put('/announcement', data)
}

// 删除公告
export function deleteAnnouncementApi(id) {
  return request.delete(`/announcement/${id}`)
}
