<template>
  <div class="home-page">
    <!-- 欢迎 + 公告 -->
    <el-row :gutter="16" class="mb-16">
      <el-col :span="16">
        <el-card shadow="never" class="welcome-card">
          <div class="welcome-area">
            <div>
              <h2>{{ greetingText }}，{{ userStore.realName }}</h2>
              <p class="welcome-sub">角色：{{ roleLabel }} | 今天是 {{ today }}</p>
              <div class="quick-links" v-if="isAdminOrHR">
                <el-button size="small" type="primary" plain @click="$router.push('/evaluation/plan')">考核方案</el-button>
                <el-button size="small" type="success" plain @click="$router.push('/result/salary')">薪酬调整</el-button>
                <el-button size="small" type="warning" plain @click="$router.push('/result/appeal')">申诉处理</el-button>
                <el-button size="small" type="info" plain @click="$router.push('/report')">报表导出</el-button>
              </div>
              <div class="quick-links" v-else-if="isManager">
                <el-button size="small" type="primary" plain @click="$router.push('/evaluation/task')">待我评分</el-button>
                <el-button size="small" type="success" plain @click="$router.push('/goal')">部门目标</el-button>
                <el-button size="small" type="warning" plain @click="$router.push('/result/appeal')">部门申诉</el-button>
                <el-button size="small" type="info" plain @click="$router.push('/result/idp')">发展计划</el-button>
              </div>
              <div class="quick-links" v-else>
                <el-button size="small" type="primary" plain @click="$router.push('/evaluation/task')">我的考核</el-button>
                <el-button size="small" type="success" plain @click="$router.push('/result/idp')">发展计划</el-button>
                <el-button size="small" type="warning" plain @click="$router.push('/result/appeal')">绩效申诉</el-button>
              </div>
            </div>
            <div class="welcome-score" v-if="myOverview.myLatestScore != null">
              <div class="score-circle" :class="'grade-' + (myOverview.myLatestGrade || '')">
                {{ myOverview.myLatestGrade || '-' }}
              </div>
              <div class="score-detail">
                <div class="score-value">{{ myOverview.myLatestScore }}</div>
                <div class="score-plan">{{ myOverview.myLatestPlanName }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="notice-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Bell /></el-icon> 最新公告</span>
              <el-button link type="primary" @click="$router.push('/announcement')">更多</el-button>
            </div>
          </template>
          <div v-if="announcements.length === 0" class="empty-text">暂无公告</div>
          <div v-for="item in announcements" :key="item.id" class="notice-item">
            <span class="notice-title">{{ item.title }}</span>
            <span class="notice-time">{{ formatNoticeTime(item.createTime) }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 管理员/HR统计卡片 -->
    <template v-if="isAdminOrHR">
      <el-row :gutter="16" class="mb-16">
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-blue" @click="$router.push('/system/user')">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.userCount || 0 }}</div><div class="stat-label">用户总数</div></div>
              <el-icon :size="36" color="#409eff"><User /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-green">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.departmentCount || 0 }}</div><div class="stat-label">部门数量</div></div>
              <el-icon :size="36" color="#67c23a"><OfficeBuilding /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-cyan">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.activePlanCount || 0 }}</div><div class="stat-label">进行中方案</div></div>
              <el-icon :size="36" color="#00b8d4"><Notebook /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-teal">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.completedTaskCount || 0 }}</div><div class="stat-label">已完成考核</div></div>
              <el-icon :size="36" color="#009688"><Finished /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-orange" @click="$router.push('/result/appeal')">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.pendingAppealCount || 0 }}</div><div class="stat-label">待处理申诉</div></div>
              <el-icon :size="36" color="#e6a23c"><EditPen /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-red" @click="$router.push('/result/salary')">
            <div class="stat-content">
              <div><div class="stat-num">{{ overview.salaryPendingCount || 0 }}</div><div class="stat-label">待审批调薪</div></div>
              <el-icon :size="36" color="#f56c6c"><Money /></el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 部门经理统计卡片 -->
    <template v-else-if="isManager">
      <el-row :gutter="16" class="mb-16">
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-blue">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptUserCount || 0 }}</div><div class="stat-label">部门人数</div></div>
              <el-icon :size="36" color="#409eff"><User /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-cyan">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptTaskCount || 0 }}</div><div class="stat-label">考核任务</div></div>
              <el-icon :size="36" color="#00b8d4"><Notebook /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-teal">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptCompletedCount || 0 }}</div><div class="stat-label">已完成考核</div></div>
              <el-icon :size="36" color="#009688"><Finished /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-green">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptAvgScore || '-' }}</div><div class="stat-label">部门均分</div></div>
              <el-icon :size="36" color="#67c23a"><TrendCharts /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-orange" @click="$router.push('/result/appeal')">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptPendingAppealCount || 0 }}</div><div class="stat-label">部门待处理申诉</div></div>
              <el-icon :size="36" color="#e6a23c"><EditPen /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="stat-card stat-purple" @click="$router.push('/result/idp')">
            <div class="stat-content">
              <div><div class="stat-num">{{ deptOverview.deptActiveIdpCount || 0 }}</div><div class="stat-label">进行中计划</div></div>
              <el-icon :size="36" color="#9c27b0"><TrendCharts /></el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 普通员工个人统计 -->
    <template v-else>
      <el-row :gutter="16" class="mb-16">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-blue">
            <div class="stat-content">
              <div><div class="stat-num">{{ myOverview.myTaskCount || 0 }}</div><div class="stat-label">考核任务</div></div>
              <el-icon :size="36" color="#409eff"><Notebook /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-green">
            <div class="stat-content">
              <div><div class="stat-num">{{ myOverview.myCompletedCount || 0 }}</div><div class="stat-label">已完成</div></div>
              <el-icon :size="36" color="#67c23a"><Finished /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-orange" @click="$router.push('/result/appeal')">
            <div class="stat-content">
              <div><div class="stat-num">{{ myOverview.myPendingAppealCount || 0 }}</div><div class="stat-label">待处理申诉</div></div>
              <el-icon :size="36" color="#e6a23c"><EditPen /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card stat-cyan" @click="$router.push('/result/idp')">
            <div class="stat-content">
              <div><div class="stat-num">{{ myOverview.myActiveIdpCount || 0 }}</div><div class="stat-label">进行中计划</div></div>
              <el-icon :size="36" color="#00b8d4"><TrendCharts /></el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 图表区域 - 方案选择 -->
    <el-row :gutter="16" class="mb-16" v-if="isAdminOrHR">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>绩效等级分布</span>
              <el-select v-model="selectedPlanId" placeholder="选择方案" size="small" style="width: 180px;" @change="loadPieChart">
                <el-option v-for="p in completedPlans" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>部门平均分对比</span>
              <el-select v-model="selectedPlanId" placeholder="选择方案" size="small" style="width: 180px;" @change="loadBarChart">
                <el-option v-for="p in completedPlans" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" v-if="isAdminOrHR">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>部门绩效排名 TOP10</span>
              <el-select v-model="selectedPlanId" placeholder="选择方案" size="small" style="width: 180px;" @change="loadRankChart">
                <el-option v-for="p in completedPlans" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
            </div>
          </template>
          <div ref="rankChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>我的绩效趋势</span></template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 个人绩效图表（所有角色） -->
    <el-row :gutter="16">
      <el-col :span="12" v-if="!isAdminOrHR">
        <el-card shadow="never">
          <template #header><span>我的绩效趋势</span></template>
          <div ref="lineChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>我的绩效雷达图</span></template>
          <div ref="radarChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { User, OfficeBuilding, Notebook, EditPen, Bell, Money, Finished, TrendCharts } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getOverviewApi, getDepartmentOverviewApi, getMyOverviewApi, getGradeDistributionApi, getDepartmentAvgApi, getScoreTrendApi, getRankingApi, getPersonalRadarApi } from '@/api/dashboard'
import { getLatestAnnouncementsApi } from '@/api/announcement'
import { getPlanListApi } from '@/api/plan'
import { getMyTasksApi } from '@/api/task'

const userStore = useUserStore()

const roleMap = { ADMIN: '系统管理员', HR: 'HR专员', MANAGER: '部门经理', EMPLOYEE: '员工' }
const roleLabel = computed(() => roleMap[userStore.role] || userStore.role)
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')
const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return '上午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

function formatNoticeTime(t) {
  if (!t) return ''
  return t.substring(0, 10)
}

const overview = ref({})
const deptOverview = ref({})
const myOverview = ref({})
const announcements = ref([])
const completedPlans = ref([])
const selectedPlanId = ref(null)

// 图表引用
const pieChartRef = ref()
const barChartRef = ref()
const lineChartRef = ref()
const rankChartRef = ref()
const radarChartRef = ref()

// 图表实例
const chartInstances = []

function createChart(domRef) {
  const chart = echarts.init(domRef.value)
  chartInstances.push(chart)
  return chart
}

onMounted(async () => {
  // 加载总览数据（按角色区分）
  if (isAdminOrHR.value) {
    try {
      const res = await getOverviewApi()
      overview.value = res.data || {}
    } catch (e) { /* ignore */ }
  } else if (isManager.value) {
    try {
      const res = await getDepartmentOverviewApi()
      deptOverview.value = res.data || {}
    } catch (e) { /* ignore */ }
  }

  // 加载个人总览
  try {
    const res = await getMyOverviewApi()
    myOverview.value = res.data || {}
  } catch (e) { /* ignore */ }

  // 加载公告
  try {
    const res = await getLatestAnnouncementsApi()
    announcements.value = (res.data || []).slice(0, 5)
  } catch (e) { /* ignore */ }

  // 获取已完成方案列表（仅ADMIN/HR需要）
  if (isAdminOrHR.value) {
    try {
      const res = await getPlanListApi({ page: 1, size: 50, status: 2 })
      completedPlans.value = res.data?.records || []
      if (completedPlans.value.length > 0) {
        selectedPlanId.value = completedPlans.value[0].id
      }
    } catch (e) { /* ignore */ }
  }

  await nextTick()
  if (isAdminOrHR.value) {
    initPieChart()
    initBarChart()
    initRankChart()
  }
  initLineChart()
  initRadarChart()
})

onBeforeUnmount(() => {
  chartInstances.forEach(c => c.dispose())
  window.removeEventListener('resize', handleResize)
})

function handleResize() {
  chartInstances.forEach(c => c.resize())
}
window.addEventListener('resize', handleResize)

// 绩效等级分布饼图
async function initPieChart() {
  const chart = createChart(pieChartRef)
  await loadPieChartData(chart)
}

async function loadPieChartData(chart) {
  if (!chart) chart = chartInstances.find(c => c.getDom() === pieChartRef.value)
  let data = [
    { name: 'A级', value: 0 },
    { name: 'B级', value: 0 },
    { name: 'C级', value: 0 },
    { name: 'D级', value: 0 }
  ]
  try {
    if (selectedPlanId.value) {
      const res = await getGradeDistributionApi(selectedPlanId.value)
      data = res.data?.data || data
    }
  } catch (e) { /* use default */ }
  chart?.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
    color: ['#67c23a', '#409eff', '#e6a23c', '#f56c6c'],
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      label: { formatter: '{b}\n{d}%' },
      data,
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.3)' } }
    }]
  })
}

function loadPieChart() { loadPieChartData() }

// 部门平均分柱状图
async function initBarChart() {
  const chart = createChart(barChartRef)
  await loadBarChartData(chart)
}

async function loadBarChartData(chart) {
  if (!chart) chart = chartInstances.find(c => c.getDom() === barChartRef.value)
  let departments = [], scores = []
  try {
    if (selectedPlanId.value) {
      const res = await getDepartmentAvgApi(selectedPlanId.value)
      departments = res.data?.departments || []
      scores = res.data?.scores || []
    }
  } catch (e) { /* use default */ }
  chart?.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c}分' },
    xAxis: { type: 'category', data: departments, axisLabel: { rotate: 30, fontSize: 11 } },
    yAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
    series: [{
      type: 'bar', data: scores, barWidth: '40%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#a0cfff' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }],
    grid: { left: 50, right: 20, bottom: 60, top: 30 }
  })
}

function loadBarChart() { loadBarChartData() }

// 绩效趋势折线图
async function initLineChart() {
  const chart = createChart(lineChartRef)
  let plans = [], myScores = []
  try {
    const res = await getScoreTrendApi(userStore.userId, new Date().getFullYear())
    const list = res.data?.data || []
    plans = list.map(i => i.planName)
    myScores = list.map(i => i.score)
  } catch (e) { /* use default */ }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: plans, axisLabel: { fontSize: 11 } },
    yAxis: { type: 'value', name: '得分', min: 0, max: 100 },
    series: [{
      type: 'line', data: myScores, smooth: true,
      lineStyle: { color: '#409eff', width: 3 },
      itemStyle: { color: '#409eff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.3)' },
          { offset: 1, color: 'rgba(64,158,255,0.05)' }
        ])
      }
    }],
    grid: { left: 50, right: 20, bottom: 30, top: 30 }
  })
}

// 部门排名
async function initRankChart() {
  const chart = createChart(rankChartRef)
  await loadRankChartData(chart)
}

async function loadRankChartData(chart) {
  if (!chart) chart = chartInstances.find(c => c.getDom() === rankChartRef.value)
  let names = [], scores = []
  try {
    if (selectedPlanId.value) {
      const res = await getRankingApi(selectedPlanId.value)
      const list = (res.data?.data || []).slice(0, 10).reverse()
      names = list.map(i => i.departmentName)
      scores = list.map(i => i.avgScore)
    }
  } catch (e) { /* use default */ }
  chart?.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
    yAxis: { type: 'category', data: names, axisLabel: { fontSize: 11 } },
    series: [{
      type: 'bar', data: scores,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#95d475' },
          { offset: 1, color: '#67c23a' }
        ]),
        borderRadius: [0, 4, 4, 0]
      },
      label: { show: true, position: 'right', formatter: '{c}' }
    }],
    grid: { left: 80, right: 40, bottom: 20, top: 20 }
  })
}

function loadRankChart() { loadRankChartData() }

// 个人雷达图
async function initRadarChart() {
  const chart = createChart(radarChartRef)
  let indicators = [], scores = []
  try {
    // 获取最新已完成任务
    const taskRes = await getMyTasksApi({ page: 1, size: 1 })
    const tasks = (taskRes.data?.records || []).filter(t => t.status === 3)
    if (tasks.length > 0) {
      const res = await getPersonalRadarApi(tasks[0].id)
      indicators = res.data?.indicators || []
      scores = res.data?.scores || []
    }
  } catch (e) { /* use default */ }

  if (indicators.length === 0) {
    indicators = ['工作质量', '工作效率', '团队协作', '创新能力', '责任心']
    scores = [0, 0, 0, 0, 0]
  }

  chart.setOption({
    tooltip: {},
    radar: {
      indicator: indicators.map(name => ({ name, max: 100 })),
      shape: 'polygon'
    },
    series: [{
      type: 'radar',
      data: [{
        value: scores,
        name: '我的得分',
        areaStyle: { color: 'rgba(64,158,255,0.2)' },
        lineStyle: { color: '#409eff' },
        itemStyle: { color: '#409eff' }
      }]
    }]
  })
}
</script>

<style scoped>
.home-page { padding: 0; }
.mb-16 { margin-bottom: 16px; }

.welcome-card { height: 100%; }
.welcome-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.welcome-area h2 { font-size: 20px; color: #303133; margin: 0; }
.welcome-sub { color: #909399; font-size: 13px; margin-top: 4px; }

.quick-links { margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap; }

.welcome-score {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}
.score-circle {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  background: #909399;
}
.score-circle.grade-A { background: #67c23a; }
.score-circle.grade-B { background: #409eff; }
.score-circle.grade-C { background: #e6a23c; }
.score-circle.grade-D { background: #f56c6c; }
.score-detail { text-align: center; }
.score-value { font-size: 28px; font-weight: 700; color: #303133; }
.score-plan { font-size: 12px; color: #909399; margin-top: 2px; max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-card { height: 100%; }
.notice-item {
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 13px;
  color: #606266;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.notice-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}
.notice-time {
  color: #c0c4cc;
  font-size: 12px;
  flex-shrink: 0;
  margin-left: 8px;
}
.empty-text { color: #909399; font-size: 13px; text-align: center; padding: 16px 0; }

.stat-card { cursor: default; transition: transform 0.2s; }
.stat-card:hover { transform: translateY(-2px); }
.stat-card .stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.stat-num { font-size: 26px; font-weight: 700; color: #303133; }
.stat-label { font-size: 12px; color: #909399; margin-top: 4px; }

.stat-blue .stat-num { color: #409eff; }
.stat-green .stat-num { color: #67c23a; }
.stat-orange .stat-num { color: #e6a23c; }
.stat-red .stat-num { color: #f56c6c; }
.stat-cyan .stat-num { color: #00b8d4; }
.stat-teal .stat-num { color: #009688; }
.stat-purple .stat-num { color: #9c27b0; }

.chart-container { width: 100%; height: 300px; }
</style>
