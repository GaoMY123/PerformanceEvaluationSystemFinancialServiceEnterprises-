import request from '@/utils/request'

// 分页查询考核任务（管理视角）
export function getTaskListApi(params) {
  return request.get('/task/page', { params })
}

// 获取我的考核任务
export function getMyTasksApi(params) {
  return request.get('/task/my', { params })
}

// 获取待我评分的任务（经理视角）
export function getPendingScoreTasksApi(params) {
  return request.get('/task/manager', { params })
}

// 获取同事互评任务
export function getPeerTasksApi(params) {
  return request.get('/task/peer', { params })
}

// 获取任务详情
export function getTaskApi(id) {
  return request.get(`/task/${id}`)
}

// 提交评分
export function submitScoreApi(data) {
  return request.post('/task/score', data)
}

// 获取任务评分明细
export function getTaskScoresApi(taskId, scoreType) {
  return request.get(`/task/score/${taskId}`, { params: { scoreType } })
}

// 获取任务附件列表
export function getTaskAttachmentsApi(taskId) {
  return request.get(`/task/attachment/${taskId}`)
}

// 上传附件
export function uploadAttachmentApi(taskId, file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('taskId', taskId)
  return request.post('/task/attachment/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 删除附件
export function deleteAttachmentApi(id) {
  return request.delete(`/task/attachment/${id}`)
}

// 查询方案的互评分配列表
export function getPeerAssignmentsApi(planId) {
  return request.get('/task/peerAssignments', { params: { planId } })
}
