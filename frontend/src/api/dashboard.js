import request from '@/utils/request'

// 系统总览数据
export function getOverviewApi() {
  return request.get('/dashboard/overview')
}

// 部门总览数据（MANAGER视角）
export function getDepartmentOverviewApi() {
  return request.get('/dashboard/departmentOverview')
}

// 个人总览数据
export function getMyOverviewApi() {
  return request.get('/dashboard/myOverview')
}

// 绩效等级分布（饼图）
export function getGradeDistributionApi(planId) {
  return request.get('/dashboard/gradeDistribution', { params: { planId } })
}

// 部门平均分（柱状图）
export function getDepartmentAvgApi(planId) {
  return request.get('/dashboard/departmentAvgScore', { params: { planId } })
}

// 个人绩效雷达图
export function getPersonalRadarApi(taskId) {
  return request.get('/dashboard/personalRadar', { params: { taskId } })
}

// 绩效趋势（折线图）
export function getScoreTrendApi(userId, year) {
  return request.get('/dashboard/scoreTrend', { params: { userId, year } })
}

// 绩效排名
export function getRankingApi(planId) {
  return request.get('/dashboard/departmentRanking', { params: { planId } })
}

// 多维度趋势分析
export function getMultiDimensionTrendApi(params) {
  return request.get('/dashboard/multiDimensionTrend', { params })
}
