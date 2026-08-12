<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="方案名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="年度">
          <el-input-number v-model="query.year" :min="2020" :max="2030" controls-position="right" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="周期">
          <el-select v-model="query.periodType" placeholder="全部" clearable style="width: 100px;">
            <el-option label="月度" value="MONTHLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="年度" value="ANNUALLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="草稿" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openDialog(null)">新增方案</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="方案名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="year" label="年度" width="70" align="center" />
        <el-table-column prop="periodType" label="周期" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ periodMap[row.periodType] || row.periodType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" align="center" />
        <el-table-column prop="endDate" label="结束日期" width="110" align="center" />
        <el-table-column label="创建人" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.creatorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" effect="light">{{ statusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="340" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openRulesDialog(row)">评分规则</el-button>
            <el-button link type="info" @click="openPeerDialog(row)" v-if="row.status >= 1">互评分配</el-button>
            <el-button link type="info" @click="openProgressDialog(row)" v-if="row.status >= 1">进度</el-button>
            <el-button link type="warning" @click="openDialog(row)" v-if="row.status === 0">编辑</el-button>
            <el-button link type="success" @click="handleStart(row)" v-if="row.status === 0">启动</el-button>
            <el-button link type="primary" @click="handleComplete(row)" v-if="row.status === 1">完成</el-button>
            <el-popconfirm title="确定删除该方案？" @confirm="handleDelete(row)" v-if="row.status === 0">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="mt-16"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="loadData"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 新增/编辑方案 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑方案' : '新增方案'" width="840px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="方案名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入方案名称" />
        </el-form-item>
        <el-form-item label="年度" prop="year">
          <el-input-number v-model="form.year" :min="2020" :max="2030" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="周期类型" prop="periodType">
          <el-select v-model="form.periodType" placeholder="请选择周期" style="width: 100%;">
            <el-option label="月度" value="MONTHLY" />
            <el-option label="季度" value="QUARTERLY" />
            <el-option label="年度" value="ANNUALLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="周期编号">
          <el-input-number v-model="form.periodNumber" :min="1" :max="12" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="考核日期" prop="dateRange">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-divider content-position="left">等级分数线</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="A级最低分">
              <el-input-number v-model="form.gradeAMin" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="B级最低分">
              <el-input-number v-model="form.gradeBMin" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="C级最低分">
              <el-input-number v-model="form.gradeCMin" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">评分权重(%)</el-divider>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="自评">
              <el-input-number v-model="form.selfWeight" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="上级评">
              <el-input-number v-model="form.managerWeight" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="互评">
              <el-input-number v-model="form.peerWeight" :min="0" :max="100" :precision="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评分规则弹窗 -->
    <el-dialog v-model="rulesDialogVisible" title="评分规则配置" width="960px" :close-on-click-modal="false">
      <el-table :data="ruleList" border size="default" v-loading="rulesLoading">
        <el-table-column prop="kpiName" label="KPI指标" width="290" show-overflow-tooltip />
        <el-table-column prop="weight" label="权重(%)" width="160" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.weight" :min="0" :max="100" :precision="1" :disabled="currentPlanStatus !== 0" style="width: 130px;" />
          </template>
        </el-table-column>
        <el-table-column prop="targetValue" label="目标值" width="160" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.targetValue" :min="0" :precision="2" :disabled="currentPlanStatus !== 0" style="width: 130px;" />
          </template>
        </el-table-column>
        <el-table-column prop="scoringFormula" label="计算方式" width="160" align="center">
          <template #default="{ row }">
            <el-select v-model="row.scoringFormula" :disabled="currentPlanStatus !== 0" style="width: 130px;">
              <el-option label="加权求和" value="WEIGHTED_SUM" />
              <el-option label="百分位" value="PERCENTILE" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="maxScore" label="满分" width="160" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.maxScore" :min="0" :precision="2" :disabled="currentPlanStatus !== 0" style="width: 130px;" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center" v-if="currentPlanStatus === 0">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该规则？" @confirm="handleDeleteRule(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px;" v-if="currentPlanStatus === 0">
        <el-divider content-position="left">添加规则</el-divider>
        <el-form :inline="true" size="default">
          <el-form-item label="KPI指标">
            <el-select v-model="newRuleKpiId" placeholder="选择指标" filterable style="width: 200px;">
              <el-option v-for="k in allKpis" :key="k.id" :label="k.name" :value="k.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addRule">添加</el-button>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="rulesDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="saveRules" v-if="currentPlanStatus === 0">保存规则</el-button>
      </template>
    </el-dialog>

    <!-- 互评分配弹窗 -->
    <el-dialog v-model="peerDialogVisible" title="互评分配详情" width="700px">
      <div v-loading="peerLoading">
        <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px;">
          启动方案时自动分配同部门互评对象（每人2人），以下为当前分配情况。
        </el-alert>
        <el-table :data="peerAssignments" border size="small" max-height="400">
          <el-table-column prop="evaluateeName" label="被评价人" min-width="100" />
          <el-table-column prop="evaluatorName" label="互评人" min-width="100" />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small" effect="light">
                {{ row.status === 1 ? '已完成' : '待互评' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="分配时间" width="170" />
        </el-table>
        <el-empty v-if="!peerLoading && peerAssignments.length === 0" description="暂无互评分配数据" />
      </div>
      <template #footer>
        <el-button @click="peerDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 进度统计弹窗 -->
    <el-dialog v-model="progressDialogVisible" title="考核进度统计" width="600px">
      <div v-loading="progressLoading">
        <el-descriptions :column="2" border size="small" v-if="progressData">
          <el-descriptions-item label="方案名称">{{ progressData.planName }}</el-descriptions-item>
          <el-descriptions-item label="方案状态">
            <el-tag :type="statusTagType(progressData.planStatus)" size="small" effect="light">{{ statusMap[progressData.planStatus] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总任务数">{{ progressData.totalCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="已完成">{{ progressData.completedCount || 0 }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">各阶段进度</el-divider>
        <div class="progress-steps" v-if="progressData">
          <div class="progress-step">
            <div class="step-label">待自评</div>
            <el-progress :percentage="progressData.selfEvalRate || 0" :stroke-width="20" :text-inside="true"
              :color="progressData.selfEvalRate >= 100 ? '#67c23a' : '#409eff'" />
            <div class="step-count">{{ progressData.totalCount - progressData.selfEvalCount }}/{{ progressData.totalCount }} 已完成</div>
          </div>
          <div class="progress-step">
            <div class="step-label">待上级评</div>
            <el-progress :percentage="progressData.managerEvalRate || 0" :stroke-width="20" :text-inside="true"
              :color="progressData.managerEvalRate >= 100 ? '#67c23a' : '#e6a23c'" />
            <div class="step-count">{{ progressData.totalCount - progressData.selfEvalCount - progressData.managerEvalCount }}/{{ progressData.totalCount }} 已完成</div>
          </div>
          <div class="progress-step">
            <div class="step-label">待互评</div>
            <el-progress :percentage="progressData.peerEvalRate || 0" :stroke-width="20" :text-inside="true"
              :color="progressData.peerEvalRate >= 100 ? '#67c23a' : '#f56c6c'" />
            <div class="step-count">{{ progressData.completedCount }}/{{ progressData.totalCount }} 已完成</div>
          </div>
        </div>

        <el-divider content-position="left">总体完成率</el-divider>
        <el-progress :percentage="progressData?.completionRate || 0" :stroke-width="24" :text-inside="true"
          :color="progressData?.completionRate >= 100 ? '#67c23a' : '#409eff'" style="margin-top: 8px;" v-if="progressData" />
      </div>
      <template #footer>
        <el-button @click="progressDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlanListApi, addPlanApi, updatePlanApi, deletePlanApi, startPlanApi, completePlanApi, getPlanRulesApi, addPlanRuleApi, updatePlanRuleApi, deletePlanRuleApi, getPlanProgressApi } from '@/api/plan'
import { getAllKpiApi } from '@/api/kpi'
import { getPeerAssignmentsApi } from '@/api/task'

const periodMap = { MONTHLY: '月度', QUARTERLY: '季度', ANNUALLY: '年度' }
const statusMap = { 0: '草稿', 1: '进行中', 2: '已完成' }
function statusTagType(s) { return { 0: 'info', 1: '', 2: 'success' }[s] || 'info' }

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', year: 2026, periodType: '', status: null, page: 1, size: 10 })
const allKpis = ref([])

onMounted(() => { loadData(); loadKpis() })

async function loadData() {
  loading.value = true
  try {
    const res = await getPlanListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function loadKpis() {
  try {
    const res = await getAllKpiApi()
    allKpis.value = res.data || []
  } catch (e) { /* ignore */ }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', year: 2026, periodType: '', status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 方案弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})
const dateRange = ref([])

watch(dateRange, (val) => {
  if (val && val.length === 2) {
    form.value.startDate = val[0]
    form.value.endDate = val[1]
  }
})

const rules = {
  name: [{ required: true, message: '请输入方案名称', trigger: 'blur' }],
  year: [{ required: true, message: '请输入年度', trigger: 'blur' }],
  periodType: [{ required: true, message: '请选择周期', trigger: 'change' }]
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    form.value = { ...row }
    dateRange.value = [row.startDate, row.endDate]
  } else {
    form.value = {
      name: '', year: 2026, periodType: 'QUARTERLY', periodNumber: 1,
      startDate: '', endDate: '', gradeAMin: 90, gradeBMin: 75, gradeCMin: 60,
      selfWeight: 20, managerWeight: 60, peerWeight: 20
    }
    dateRange.value = []
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updatePlanApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addPlanApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function handleStart(row) {
  await ElMessageBox.confirm('启动后将生成考核任务，是否继续？', '启动方案', { type: 'warning' })
  await startPlanApi(row.id)
  ElMessage.success('方案已启动')
  loadData()
}

async function handleComplete(row) {
  await ElMessageBox.confirm('完成后将汇总得分生成等级，是否继续？', '完成方案', { type: 'warning' })
  await completePlanApi(row.id)
  ElMessage.success('方案已完成')
  loadData()
}

async function handleDelete(row) {
  await deletePlanApi(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// 评分规则
const rulesDialogVisible = ref(false)
const rulesLoading = ref(false)
const ruleList = ref([])
const currentPlanId = ref(null)
const currentPlanStatus = ref(0)
const newRuleKpiId = ref(null)

async function openRulesDialog(row) {
  currentPlanId.value = row.id
  currentPlanStatus.value = row.status
  rulesDialogVisible.value = true
  rulesLoading.value = true
  try {
    const res = await getPlanRulesApi(row.id)
    ruleList.value = res.data || []
  } catch (e) { ruleList.value = [] }
  finally { rulesLoading.value = false }
}

async function addRule() {
  if (!newRuleKpiId.value) {
    ElMessage.warning('请选择KPI')
    return
  }
  const kpi = allKpis.value.find(k => k.id === newRuleKpiId.value)
  if (!kpi) return
  // 检查是否已存在
  if (ruleList.value.some(r => r.kpiId === kpi.id)) {
    ElMessage.warning('该KPI已添加')
    return
  }
  const newRule = {
    planId: currentPlanId.value, kpiId: kpi.id, kpiName: kpi.name,
    weight: 10, targetValue: kpi.targetValue, scoringFormula: 'WEIGHTED_SUM', maxScore: 100
  }
  await addPlanRuleApi(newRule)
  ElMessage.success('规则已添加')
  newRuleKpiId.value = null
  // 刷新规则列表
  const res = await getPlanRulesApi(currentPlanId.value)
  ruleList.value = res.data || []
}

async function handleDeleteRule(row) {
  await deletePlanRuleApi(row.id)
  ElMessage.success('规则已删除')
  const res = await getPlanRulesApi(currentPlanId.value)
  ruleList.value = res.data || []
}

async function saveRules() {
  // 逐条更新修改过的规则
  for (const rule of ruleList.value) {
    if (rule.id) {
      await updatePlanRuleApi(rule)
    }
  }
  ElMessage.success('规则保存成功')
}

// 互评分配
const peerDialogVisible = ref(false)
const peerLoading = ref(false)
const peerAssignments = ref([])

async function openPeerDialog(row) {
  peerDialogVisible.value = true
  peerLoading.value = true
  peerAssignments.value = []
  try {
    const res = await getPeerAssignmentsApi(row.id)
    peerAssignments.value = res.data || []
  } catch (e) {
    peerAssignments.value = []
  } finally {
    peerLoading.value = false
  }
}

// 进度统计
const progressDialogVisible = ref(false)
const progressLoading = ref(false)
const progressData = ref(null)

async function openProgressDialog(row) {
  progressDialogVisible.value = true
  progressLoading.value = true
  progressData.value = null
  try {
    const res = await getPlanProgressApi(row.id)
    progressData.value = res.data || null
  } catch (e) {
    progressData.value = null
  } finally {
    progressLoading.value = false
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}
.mt-16 {
  margin-top: 16px;
  justify-content: flex-end;
}
.progress-steps {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.progress-step {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.step-label {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}
.step-count {
  font-size: 12px;
  color: #909399;
}
</style>
