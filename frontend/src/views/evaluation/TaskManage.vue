<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 角色不同显示不同标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的考核" name="my" />
        <el-tab-pane label="待我评分" name="pending" v-if="isManagerOrHR" />
        <el-tab-pane label="同事互评" name="peer" />
        <el-tab-pane label="全部任务" name="all" v-if="isAdminOrHR" />
      </el-tabs>

      <!-- 筛选栏（全部任务tab） -->
      <el-form :inline="true" :model="query" class="search-form" v-if="activeTab === 'all'">
        <el-form-item label="方案">
          <el-select v-model="query.planId" placeholder="全部" clearable style="width: 180px;">
            <el-option v-for="p in plans" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部" clearable style="width: 140px;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 110px;">
            <el-option label="待自评" :value="0" />
            <el-option label="待上级评" :value="1" />
            <el-option label="待互评" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="planName" label="考核方案" min-width="180" show-overflow-tooltip />
        <el-table-column prop="userName" label="被考核人" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="departmentName" label="部门" width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ row.departmentName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="selfScore" label="自评" width="70" align="center">
          <template #default="{ row }">{{ row.selfScore ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="managerScore" label="上级评" width="80" align="center">
          <template #default="{ row }">{{ row.managerScore ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="peerScore" label="互评" width="70" align="center">
          <template #default="{ row }">{{ row.peerScore ?? '-' }}</template>
        </el-table-column>
        <el-table-column prop="finalScore" label="最终得分" width="90" align="center">
          <template #default="{ row }">
            <span :class="scoreClass(row.finalScore)">{{ row.finalScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="等级" width="70" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.grade" :type="gradeTagType(row.grade)" size="small" effect="light">{{ row.grade }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="taskStatusTagType(row.status)" size="small" effect="light">{{ taskStatusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button link type="success" @click="openScoreDialog(row, 'SELF')"
              v-if="activeTab === 'my' && canSelfScore(row)">自评</el-button>
            <el-button link type="warning" @click="openScoreDialog(row, 'MANAGER')"
              v-if="activeTab === 'pending' && canManagerScore(row)">评分</el-button>
            <el-button link type="primary" @click="openScoreDialog(row, 'PEER')"
              v-if="activeTab === 'peer' && canPeerScore(row)">互评</el-button>
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

    <!-- 任务详情弹窗 -->
    <el-dialog v-model="detailVisible" title="考核任务详情" width="750px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="考核方案">{{ currentTask.planName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="被考核人">{{ currentTask.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentTask.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="taskStatusTagType(currentTask.status)" size="small" effect="light">{{ taskStatusMap[currentTask.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="自评分">{{ currentTask.selfScore ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="上级评分">{{ currentTask.managerScore ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="互评分">{{ currentTask.peerScore ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="最终得分">
          <span :class="scoreClass(currentTask.finalScore)">{{ currentTask.finalScore ?? '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag v-if="currentTask.grade" :type="gradeTagType(currentTask.grade)" size="small" effect="light">{{ currentTask.grade }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentTask.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">评分明细</el-divider>
      <el-table :data="taskScores" border size="small" v-loading="scoresLoading">
        <el-table-column prop="kpiName" label="KPI指标" min-width="160" show-overflow-tooltip />
        <el-table-column prop="scoreType" label="评分类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ scoreTypeMap[row.scoreType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="evaluatorName" label="评分人" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.evaluatorName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="70" align="center" />
        <el-table-column prop="comment" label="评语" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.comment || '-' }}</template>
        </el-table-column>
      </el-table>

      <el-divider content-position="left">附件</el-divider>
      <el-upload v-if="currentTask.status < 3" :http-request="handleUpload" :show-file-list="false"
        accept=".doc,.docx,.pdf,.xls,.xlsx,.png,.jpg">
        <el-button size="small" type="primary">上传附件</el-button>
      </el-upload>
      <el-table :data="taskAttachments" border size="small" style="margin-top: 8px;" v-if="taskAttachments.length > 0">
        <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileSize" label="大小" width="90" align="center">
          <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="170" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleDownloadAttachment(row)">下载</el-button>
            <el-popconfirm title="确定删除该附件？" @confirm="handleDeleteAttachment(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div v-else style="color: #909399; font-size: 13px; padding: 8px 0;">暂无附件</div>
    </el-dialog>

    <!-- 评分弹窗 -->
    <el-dialog v-model="scoreDialogVisible" :title="scoreTitle" width="640px" :close-on-click-modal="false">
      <el-table :data="scoreFormList" border size="small">
        <el-table-column prop="kpiName" label="KPI指标" min-width="160" show-overflow-tooltip />
        <el-table-column prop="targetValue" label="目标值" width="90" align="center" />
        <el-table-column label="得分" width="130" align="center">
          <template #default="{ row }">
            <el-input-number v-model="row.score" size="small" :min="0" :max="row.maxScore || 100" :precision="1" style="width: 110px;" />
          </template>
        </el-table-column>
        <el-table-column label="评语" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.comment" size="small" placeholder="选填" />
          </template>
        </el-table-column>
      </el-table>
      <el-form-item label="总体评语" style="margin-top: 16px;" label-width="80px">
        <el-input v-model="scoreRemark" type="textarea" :rows="2" placeholder="选填" />
      </el-form-item>
      <template #footer>
        <el-button @click="scoreDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="scoreLoading" @click="handleSubmitScore">提交评分</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getTaskListApi, getMyTasksApi, getPendingScoreTasksApi, getPeerTasksApi, submitScoreApi, getTaskScoresApi, getTaskAttachmentsApi, uploadAttachmentApi, deleteAttachmentApi } from '@/api/task'
import { getPlanRulesApi } from '@/api/plan'
import { getPlanListApi } from '@/api/plan'
import { getAllDepartmentsApi } from '@/api/department'

const userStore = useUserStore()
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManagerOrHR = computed(() => ['ADMIN', 'HR', 'MANAGER'].includes(userStore.role))

const taskStatusMap = { 0: '待自评', 1: '待上级评', 2: '待互评', 3: '已完成' }
function taskStatusTagType(s) { return { 0: 'info', 1: '', 2: 'warning', 3: 'success' }[s] || 'info' }
function gradeTagType(g) { return { A: 'success', B: '', C: 'warning', D: 'danger' }[g] || 'info' }
function scoreClass(s) { return s >= 90 ? 'score-high' : s >= 75 ? 'score-mid' : s >= 60 ? 'score-low' : 'score-fail' }
const scoreTypeMap = { SELF: '自评', MANAGER: '上级评', PEER: '互评' }

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

function formatFileSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + 'B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
  return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ planId: null, departmentId: null, status: null, page: 1, size: 10 })
const activeTab = ref('my')
const plans = ref([])
const departments = ref([])

onMounted(() => {
  loadData()
  loadPlans()
  loadDepartments()
})

async function loadPlans() {
  try {
    const res = await getPlanListApi({ page: 1, size: 100 })
    plans.value = res.data?.records || []
  } catch (e) { /* ignore */ }
}

async function loadDepartments() {
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }
}

function handleTabChange() {
  query.value.page = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'my') {
      res = await getMyTasksApi(query.value)
    } else if (activeTab.value === 'pending') {
      res = await getPendingScoreTasksApi(query.value)
    } else if (activeTab.value === 'peer') {
      res = await getPeerTasksApi(query.value)
    } else {
      res = await getTaskListApi(query.value)
    }
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { planId: null, departmentId: null, status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 状态0=待自评 → 可以自评；状态1=待上级评 → 上级可评；状态2=待互评 → 同事可互评
function canSelfScore(row) { return row.status === 0 }
function canManagerScore(row) { return row.status === 1 }
function canPeerScore(row) { return row.status === 2 }

// 详情
const detailVisible = ref(false)
const currentTask = ref({})
const taskScores = ref([])
const taskAttachments = ref([])
const scoresLoading = ref(false)

async function openDetail(row) {
  currentTask.value = row
  detailVisible.value = true
  scoresLoading.value = true
  try {
    const [scoresRes, attachRes] = await Promise.all([
      getTaskScoresApi(row.id),
      getTaskAttachmentsApi(row.id)
    ])
    taskScores.value = scoresRes.data || []
    taskAttachments.value = attachRes.data || []
  } catch (e) {
    taskScores.value = []
    taskAttachments.value = []
  } finally { scoresLoading.value = false }
}

async function handleUpload(options) {
  try {
    await uploadAttachmentApi(currentTask.value.id, options.file)
    ElMessage.success('上传成功')
    const res = await getTaskAttachmentsApi(currentTask.value.id)
    taskAttachments.value = res.data || []
  } catch (e) { /* error handled by interceptor */ }
}

function handleDownloadAttachment(row) {
  const url = row.filePath.startsWith('http') ? row.filePath : `/api${row.filePath}`
  const link = document.createElement('a')
  link.href = url
  link.download = row.fileName || '附件'
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

async function handleDeleteAttachment(row) {
  await deleteAttachmentApi(row.id)
  ElMessage.success('删除成功')
  const res = await getTaskAttachmentsApi(currentTask.value.id)
  taskAttachments.value = res.data || []
}

// 评分
const scoreDialogVisible = ref(false)
const scoreType = ref('SELF')
const scoreFormList = ref([])
const scoreLoading = ref(false)
const currentScoreTaskId = ref(null)
const scoreRemark = ref('')

const scoreTitle = computed(() => {
  return { SELF: '自评打分', MANAGER: '上级评分', PEER: '同事互评' }[scoreType.value] || '评分'
})

async function openScoreDialog(row, type) {
  scoreType.value = type
  currentScoreTaskId.value = row.id
  scoreRemark.value = ''
  scoreDialogVisible.value = true
  try {
    const res = await getPlanRulesApi(row.planId)
    scoreFormList.value = (res.data || []).map(r => ({
      kpiId: r.kpiId, kpiName: r.kpiName || '指标' + r.kpiId,
      targetValue: r.targetValue, maxScore: r.maxScore, score: null, comment: ''
    }))
  } catch (e) { scoreFormList.value = [] }
}

async function handleSubmitScore() {
  const hasEmpty = scoreFormList.value.some(s => s.score === null || s.score === undefined)
  if (hasEmpty) {
    ElMessage.warning('请为所有指标打分')
    return
  }
  scoreLoading.value = true
  try {
    await submitScoreApi({
      taskId: currentScoreTaskId.value,
      scoreType: scoreType.value,
      remark: scoreRemark.value || null,
      scores: scoreFormList.value.map(s => ({
        kpiId: s.kpiId, score: s.score, comment: s.comment
      }))
    })
    ElMessage.success('评分提交成功')
    scoreDialogVisible.value = false
    loadData()
  } finally { scoreLoading.value = false }
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
.score-high { color: #67c23a; font-weight: 700; }
.score-mid { color: #409eff; font-weight: 700; }
.score-low { color: #e6a23c; font-weight: 700; }
.score-fail { color: #f56c6c; font-weight: 700; }
</style>
