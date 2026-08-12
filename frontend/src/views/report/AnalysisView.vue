<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="年度">
          <el-input-number v-model="query.year" :min="2020" :max="2030" controls-position="right" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="周期">
          <el-select v-model="query.periodType" placeholder="全部周期" clearable style="width: 120px;">
            <el-option label="月度" value="MONTHLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="年度" value="ANNUALLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部部门" clearable style="width: 160px;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 部门绩效趋势折线图 -->
    <el-card shadow="never" style="margin-bottom: 16px;">
      <template #header>
        <span>部门绩效趋势对比</span>
      </template>
      <div ref="trendChartRef" style="height: 380px;" v-loading="trendLoading"></div>
      <el-empty v-if="!trendLoading && trendData.length === 0" description="暂无数据，请调整筛选条件" />
    </el-card>

    <!-- 下方两列 -->
    <el-row :gutter="16">
      <!-- 部门排名对比 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header-row">
              <span>部门排名对比</span>
              <el-select v-model="rankingPlanId" placeholder="选择考核方案" size="small" style="width: 200px;" @change="loadRanking">
                <el-option v-for="p in completedPlans" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
          </template>
          <div ref="rankingChartRef" style="height: 320px;" v-loading="rankingLoading"></div>
          <el-empty v-if="!rankingLoading && !rankingPlanId" description="请选择考核方案" />
        </el-card>
      </el-col>

      <!-- 等级分布 -->
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header-row">
              <span>绩效等级分布</span>
              <el-select v-model="gradePlanId" placeholder="选择考核方案" size="small" style="width: 200px;" @change="loadGradeDist">
                <el-option v-for="p in completedPlans" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
          </template>
          <div ref="gradeChartRef" style="height: 320px;" v-loading="gradeLoading"></div>
          <el-empty v-if="!gradeLoading && !gradePlanId" description="请选择考核方案" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMultiDimensionTrendApi, getRankingApi, getGradeDistributionApi } from '@/api/dashboard'
import { getPlanListApi } from '@/api/plan'
import { getAllDepartmentsApi } from '@/api/department'
import * as echarts from 'echarts'

const departments = ref([])
const completedPlans = ref([])
const query = ref({ year: new Date().getFullYear(), periodType: '', departmentId: null })

// 趋势图
const trendChartRef = ref()
const trendLoading = ref(false)
const trendData = ref([])
let trendChart = null

// 排名图
const rankingChartRef = ref()
const rankingLoading = ref(false)
const rankingPlanId = ref(null)
let rankingChart = null

// 等级分布图
const gradeChartRef = ref()
const gradeLoading = ref(false)
const gradePlanId = ref(null)
let gradeChart = null

onMounted(async () => {
  try {
    const [planRes, deptRes] = await Promise.all([
      getPlanListApi({ page: 1, size: 100 }),
      getAllDepartmentsApi()
    ])
    completedPlans.value = (planRes.data?.records || []).filter(p => p.status === 2)
    departments.value = deptRes.data || []
  } catch (e) { /* ignore */ }

  await nextTick()
  trendChart = echarts.init(trendChartRef.value)
  rankingChart = echarts.init(rankingChartRef.value)
  gradeChart = echarts.init(gradeChartRef.value)

  window.addEventListener('resize', handleResize)
  loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  rankingChart?.dispose()
  gradeChart?.dispose()
})

function handleResize() {
  trendChart?.resize()
  rankingChart?.resize()
  gradeChart?.resize()
}

function handleReset() {
  query.value = { year: new Date().getFullYear(), periodType: '', departmentId: null }
  loadData()
}

// ---- 趋势图 ----
async function loadData() {
  trendLoading.value = true
  try {
    const params = {}
    if (query.value.year) params.year = query.value.year
    if (query.value.periodType) params.periodType = query.value.periodType
    if (query.value.departmentId) params.departmentId = query.value.departmentId
    const res = await getMultiDimensionTrendApi(params)
    trendData.value = res.data?.series || []
    renderTrendChart(res.data)
  } catch (e) {
    trendData.value = []
  } finally {
    trendLoading.value = false
  }
}

function renderTrendChart(data) {
  if (!data || !trendChart) return
  const periods = data.periods || []
  const series = data.series || []
  const deptNames = data.departments || []

  // 收集所有部门名
  const allDepts = new Set()
  series.forEach(s => {
    Object.keys(s.deptAvg || {}).forEach(d => allDepts.add(d))
  })
  const deptList = Array.from(allDepts)

  // 颜色列表
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9c27b0', '#00bcd4', '#ff9800']

  const option = {
    tooltip: { trigger: 'axis' },
    legend: {
      data: [...deptList, '公司均分'],
      bottom: 0,
      type: 'scroll'
    },
    grid: { left: 50, right: 30, top: 30, bottom: 50 },
    xAxis: {
      type: 'category',
      data: periods,
      axisLabel: { rotate: periods.length > 6 ? 30 : 0 }
    },
    yAxis: {
      type: 'value',
      name: '平均分',
      min: 0,
      max: 100
    },
    series: [
      ...deptList.map((dept, i) => ({
        name: dept,
        type: 'line',
        data: series.map(s => s.deptAvg?.[dept] ?? null),
        smooth: true,
        itemStyle: { color: colors[i % colors.length] }
      })),
      {
        name: '公司均分',
        type: 'line',
        data: series.map(s => s.totalAvg ?? null),
        smooth: true,
        lineStyle: { width: 3, type: 'dashed' },
        itemStyle: { color: '#303133' }
      }
    ]
  }
  trendChart.setOption(option, true)
}

// ---- 排名图 ----
async function loadRanking() {
  if (!rankingPlanId.value) return
  rankingLoading.value = true
  try {
    const res = await getRankingApi(rankingPlanId.value)
    const data = res.data?.data || []
    const sorted = [...data].sort((a, b) => b.avgScore - a.avgScore)
    const option = {
      tooltip: { trigger: 'axis' },
      grid: { left: 100, right: 30, top: 20, bottom: 30 },
      xAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
      yAxis: {
        type: 'category',
        data: sorted.map(d => d.departmentName).reverse(),
        axisLabel: { fontSize: 12 }
      },
      series: [{
        type: 'bar',
        data: sorted.map(d => d.avgScore).reverse(),
        itemStyle: {
          color: (params) => {
            const val = params.value
            return val >= 90 ? '#67c23a' : val >= 75 ? '#409eff' : val >= 60 ? '#e6a23c' : '#f56c6c'
          }
        },
        label: { show: true, position: 'right', formatter: '{c}' }
      }]
    }
    rankingChart.setOption(option, true)
  } catch (e) { /* ignore */ }
  finally { rankingLoading.value = false }
}

// ---- 等级分布图 ----
async function loadGradeDist() {
  if (!gradePlanId.value) return
  gradeLoading.value = true
  try {
    const res = await getGradeDistributionApi(gradePlanId.value)
    const data = res.data?.data || []
    const colorMap = { 'A级': '#67c23a', 'B级': '#409eff', 'C级': '#e6a23c', 'D级': '#f56c6c' }
    const option = {
      tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        data: data.map(d => ({
          name: d.name,
          value: d.value,
          itemStyle: { color: colorMap[d.name] || '#909399' }
        })),
        label: { formatter: '{b}\n{c}人' },
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' } }
      }]
    }
    gradeChart.setOption(option, true)
  } catch (e) { /* ignore */ }
  finally { gradeLoading.value = false }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 0;
}
.card-header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
