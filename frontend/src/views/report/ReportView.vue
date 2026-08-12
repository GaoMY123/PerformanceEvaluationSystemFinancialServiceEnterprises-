<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 筛选栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="考核方案">
          <el-select v-model="query.planId" placeholder="请选择考核方案" style="width: 280px;" @change="handlePlanChange">
            <el-option v-for="p in plans" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部" clearable style="width: 160px;" @change="loadPreview">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button :icon="Refresh" @click="loadPreview" :disabled="!query.planId">刷新预览</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <div class="stats-row" v-if="stats">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total || 0 }}</div>
          <div class="stat-label">参评人数</div>
        </el-card>
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.avgScore || 0 }}</div>
          <div class="stat-label">平均得分</div>
        </el-card>
        <el-card shadow="hover" class="stat-card" v-for="(count, grade) in stats.gradeDist || {}" :key="grade">
          <div class="stat-value" :class="'grade-' + grade">{{ count }}</div>
          <div class="stat-label">{{ grade }} 等级</div>
        </el-card>
      </div>

      <!-- 部门平均分 -->
      <el-card shadow="never" class="dept-card" v-if="stats && stats.deptAvg && Object.keys(stats.deptAvg).length">
        <template #header><span>部门平均得分</span></template>
        <div class="dept-bars">
          <div class="dept-bar-item" v-for="(avg, name) in stats.deptAvg" :key="name">
            <span class="dept-name">{{ name }}</span>
            <el-progress :percentage="avg" :stroke-width="18" :text-inside="true"
              :color="avg >= 90 ? '#67c23a' : avg >= 75 ? '#409eff' : avg >= 60 ? '#e6a23c' : '#f56c6c'" style="flex: 1;" />
          </div>
        </div>
      </el-card>

      <!-- 预览表格 -->
      <el-table :data="previewData" v-loading="loading" stripe border style="width: 100%; margin-top: 16px;" :scrollbar-always-on="true"
        v-if="query.planId" max-height="420">
        <el-table-column prop="seq" label="序号" width="60" align="center" />
        <el-table-column prop="userName" label="员工姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="departmentName" label="所属部门" width="140" show-overflow-tooltip />
        <el-table-column prop="selfScore" label="自评分" width="90" align="center" />
        <el-table-column prop="managerScore" label="上级评分" width="90" align="center" />
        <el-table-column prop="peerScore" label="同事互评分" width="100" align="center" />
        <el-table-column prop="finalScore" label="最终得分" width="90" align="center">
          <template #default="{ row }">
            <span :class="scoreClass(row.finalScore)">{{ row.finalScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="绩效等级" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.grade" :type="gradeTagType(row.grade)" size="small" effect="light">{{ row.grade }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!query.planId" description="请选择考核方案以预览数据" />

      <!-- 导出按钮区 -->
      <div class="export-actions" v-if="query.planId">
        <el-card shadow="hover" class="export-card" @click="handleExportExcel">
          <el-icon :size="40" color="#67c23a"><Document /></el-icon>
          <h3>考核结果 Excel</h3>
          <p>导出绩效考核结果汇总表</p>
        </el-card>
        <el-card shadow="hover" class="export-card" @click="handleExportPdf">
          <el-icon :size="40" color="#f56c6c"><Printer /></el-icon>
          <h3>考核结果 PDF</h3>
          <p>导出绩效考核报告</p>
        </el-card>
        <el-card shadow="hover" class="export-card" @click="handleExportSalary">
          <el-icon :size="40" color="#e6a23c"><Money /></el-icon>
          <h3>薪酬调整 Excel</h3>
          <p>导出薪酬调整汇总表</p>
        </el-card>
        <el-card shadow="hover" class="export-card" @click="handleExportIdp">
          <el-icon :size="40" color="#409eff"><TrendCharts /></el-icon>
          <h3>发展计划 Excel</h3>
          <p>导出个人发展计划汇总</p>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, Printer, Money, TrendCharts, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { previewReportApi, getReportStatsApi, exportExcelApi, exportPdfApi, exportSalaryExcelApi, exportIdpExcelApi } from '@/api/report'
import { getPlanListApi } from '@/api/plan'
import { getAllDepartmentsApi } from '@/api/department'

function gradeTagType(g) { return { A: 'success', B: '', C: 'warning', D: 'danger' }[g] || 'info' }
function scoreClass(s) { return s >= 90 ? 'score-high' : s >= 75 ? 'score-mid' : s >= 60 ? 'score-low' : 'score-fail' }

const loading = ref(false)
const plans = ref([])
const departments = ref([])
const query = ref({ planId: null, departmentId: null })
const previewData = ref([])
const stats = ref(null)

onMounted(async () => {
  try {
    const [planRes, deptRes] = await Promise.all([
      getPlanListApi({ page: 1, size: 100 }),
      getAllDepartmentsApi()
    ])
    plans.value = (planRes.data?.records || []).filter(p => p.status === 2 || p.status === 3)
    departments.value = deptRes.data || []
  } catch (e) { /* ignore */ }
})

async function handlePlanChange() {
  stats.value = null
  previewData.value = []
  if (query.value.planId) {
    loadPreview()
    loadStats()
  }
}

async function loadPreview() {
  if (!query.value.planId) return
  loading.value = true
  try {
    const res = await previewReportApi(query.value.planId, query.value.departmentId)
    previewData.value = res.data || []
  } finally { loading.value = false }
}

async function loadStats() {
  if (!query.value.planId) return
  try {
    const res = await getReportStatsApi(query.value.planId)
    stats.value = res.data || null
  } catch (e) { stats.value = null }
}

function downloadFile(response, filename) {
  const blob = new Blob([response.data], { type: response.headers['content-type'] })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  window.URL.revokeObjectURL(url)
}

async function handleExportExcel() {
  if (!query.value.planId) { ElMessage.warning('请先选择考核方案'); return }
  try {
    const res = await exportExcelApi(query.value.planId, query.value.departmentId)
    downloadFile(res, '绩效考核结果.xlsx')
    ElMessage.success('导出成功')
  } catch (e) { /* handled by interceptor */ }
}

async function handleExportPdf() {
  if (!query.value.planId) { ElMessage.warning('请先选择考核方案'); return }
  try {
    const res = await exportPdfApi(query.value.planId, query.value.departmentId)
    downloadFile(res, '绩效考核报告.pdf')
    ElMessage.success('导出成功')
  } catch (e) { /* handled by interceptor */ }
}

async function handleExportSalary() {
  if (!query.value.planId) { ElMessage.warning('请先选择考核方案'); return }
  try {
    const res = await exportSalaryExcelApi(query.value.planId)
    downloadFile(res, '薪酬调整汇总.xlsx')
    ElMessage.success('导出成功')
  } catch (e) { /* handled by interceptor */ }
}

async function handleExportIdp() {
  if (!query.value.planId) { ElMessage.warning('请先选择考核方案'); return }
  try {
    const res = await exportIdpExcelApi(query.value.planId)
    downloadFile(res, '发展计划汇总.xlsx')
    ElMessage.success('导出成功')
  } catch (e) { /* handled by interceptor */ }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.stat-card {
  min-width: 120px;
  text-align: center;
  flex: 0 0 auto;
}
.stat-card .stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
}
.stat-card .stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}
.grade-A { color: #67c23a !important; }
.grade-B { color: #409eff !important; }
.grade-C { color: #e6a23c !important; }
.grade-D { color: #f56c6c !important; }

.dept-card {
  margin-bottom: 16px;
}
.dept-bars {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.dept-bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.dept-name {
  width: 100px;
  text-align: right;
  font-size: 13px;
  color: #606266;
  flex-shrink: 0;
}

.export-actions {
  display: flex;
  gap: 20px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.export-card {
  flex: 1;
  min-width: 180px;
  text-align: center;
  cursor: pointer;
  padding: 16px;
  transition: transform 0.2s;
}
.export-card:hover {
  transform: translateY(-4px);
}
.export-card h3 {
  margin: 8px 0 6px;
  font-size: 15px;
  color: #303133;
}
.export-card p {
  color: #909399;
  font-size: 12px;
}

.score-high { color: #67c23a; font-weight: 700; }
.score-mid { color: #409eff; font-weight: 700; }
.score-low { color: #e6a23c; font-weight: 700; }
.score-fail { color: #f56c6c; font-weight: 700; }
</style>
