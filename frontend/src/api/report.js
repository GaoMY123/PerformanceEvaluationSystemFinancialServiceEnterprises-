import request from '@/utils/request'

// 预览考核结果数据
export function previewReportApi(planId, departmentId) {
  return request.get(`/report/preview/${planId}`, { params: { departmentId } })
}

// 获取统计摘要
export function getReportStatsApi(planId) {
  return request.get(`/report/stats/${planId}`)
}

// 导出考核结果Excel
export function exportExcelApi(planId, departmentId) {
  return request.get(`/report/exportExcel/${planId}`, { params: { departmentId }, responseType: 'blob' })
}

// 导出考核结果PDF
export function exportPdfApi(planId, departmentId) {
  return request.get(`/report/exportPdf/${planId}`, { params: { departmentId }, responseType: 'blob' })
}

// 导出薪酬调整Excel
export function exportSalaryExcelApi(planId) {
  return request.get(`/report/exportSalaryExcel/${planId}`, { responseType: 'blob' })
}

// 导出发展计划Excel
export function exportIdpExcelApi(planId) {
  return request.get(`/report/exportIdpExcel/${planId}`, { responseType: 'blob' })
}
