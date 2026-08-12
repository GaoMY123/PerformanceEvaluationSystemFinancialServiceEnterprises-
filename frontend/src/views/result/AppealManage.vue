<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的申诉" name="my" />
        <el-tab-pane label="部门申诉" name="department" v-if="isManager" />
        <el-tab-pane label="全部申诉" name="all" v-if="isAdminOrHR" />
      </el-tabs>

      <!-- 筛选栏（全部申诉tab） -->
      <el-form :inline="true" :model="query" class="search-form" v-if="activeTab === 'all'">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="申诉人姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部" clearable style="width: 140px;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 筛选栏（部门申诉tab） -->
      <el-form :inline="true" :model="deptQuery" class="search-form" v-if="activeTab === 'department'">
        <el-form-item label="状态">
          <el-select v-model="deptQuery.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleDeptReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 提交申诉按钮（我的申诉tab） -->
      <div style="margin-bottom: 12px;" v-if="activeTab === 'my'">
        <el-button type="success" :icon="Plus" @click="openSubmitDialog">提交申诉</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="planName" label="考核方案" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.planName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="userName" label="申诉人" width="100" show-overflow-tooltip v-if="activeTab !== 'my'">
          <template #default="{ row }">{{ row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="departmentName" label="部门" width="120" show-overflow-tooltip v-if="activeTab !== 'my'">
          <template #default="{ row }">{{ row.departmentName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="reason" label="申诉理由" min-width="250" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="appealStatusTagType(row.status)" size="small" effect="light">{{ appealStatusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.handlerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="handleTime" label="处理时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.handleTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button link type="warning" @click="openHandleDialog(row)"
              v-if="(isAdminOrHR || isManager) && (row.status === 0 || row.status === 1)">处理</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申诉详情" width="650px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="考核方案">{{ currentRow.planName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申诉人">{{ currentRow.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentRow.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="appealStatusTagType(currentRow.status)" size="small" effect="light">{{ appealStatusMap[currentRow.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申诉理由" :span="2">{{ currentRow.reason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRow.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ formatTime(currentRow.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ currentRow.reply || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ formatTime(currentRow.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatTime(currentRow.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 提交申诉弹窗 -->
    <el-dialog v-model="submitDialogVisible" title="提交绩效申诉" width="500px" :close-on-click-modal="false" @close="resetSubmitForm">
      <el-form ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="90px">
        <el-form-item label="考核任务" prop="taskId">
          <el-select v-model="submitForm.taskId" placeholder="请选择考核任务" style="width: 100%;">
            <el-option v-for="t in myCompletedTasks" :key="t.id"
              :label="t.planName + ' - ' + t.grade + '(' + t.finalScore + '分)'" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申诉理由" prop="reason">
          <el-input v-model="submitForm.reason" type="textarea" :rows="4" placeholder="请详细说明申诉理由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitAppeal">提交</el-button>
      </template>
    </el-dialog>

    <!-- 处理申诉弹窗 -->
    <el-dialog v-model="handleDialogVisible" title="处理申诉" width="550px" :close-on-click-modal="false" @close="resetHandleForm">
      <el-descriptions :column="2" border size="small" class="mb-16">
        <el-descriptions-item label="申诉人">{{ currentAppeal.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentAppeal.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申诉理由" :span="2">{{ currentAppeal.reason || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="90px">
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="handleForm.status">
            <el-radio :value="2">通过（调整得分）</el-radio>
            <el-radio :value="3">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复意见" prop="reply">
          <el-input v-model="handleForm.reply" type="textarea" :rows="4" placeholder="请输入处理意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="handleLoading" @click="handleSubmitHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getAppealListApi, getMyAppealsApi, getDepartmentAppealsApi, addAppealApi, handleAppealApi } from '@/api/appeal'
import { getMyTasksApi } from '@/api/task'
import { getAllDepartmentsApi } from '@/api/department'

const userStore = useUserStore()
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')

const appealStatusMap = { 0: '待处理', 1: '处理中', 2: '已通过', 3: '已驳回' }
function appealStatusTagType(s) { return { 0: 'warning', 1: '', 2: 'success', 3: 'danger' }[s] || 'info' }

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', departmentId: null, status: null, page: 1, size: 10 })
const deptQuery = ref({ status: null, page: 1, size: 10 })
const activeTab = ref('my')
const departments = ref([])

onMounted(async () => {
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }
  loadData()
})

function handleTabChange() {
  query.value.page = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'my') {
      res = await getMyAppealsApi(query.value)
    } else if (activeTab.value === 'department') {
      res = await getDepartmentAppealsApi(deptQuery.value)
    } else {
      res = await getAppealListApi(query.value)
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
  query.value = { keyword: '', departmentId: null, status: null, page: 1, size: 10 }
  loadData()
}

function handleDeptReset() {
  deptQuery.value = { status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 详情
const detailVisible = ref(false)
const currentRow = ref({})
function viewDetail(row) {
  currentRow.value = row
  detailVisible.value = true
}

// 提交申诉
const submitDialogVisible = ref(false)
const submitFormRef = ref()
const submitLoading = ref(false)
const submitForm = ref({ taskId: null, reason: '' })
const myCompletedTasks = ref([])

const submitRules = {
  taskId: [{ required: true, message: '请选择考核任务', trigger: 'change' }],
  reason: [{ required: true, message: '请输入申诉理由', trigger: 'blur' }]
}

async function openSubmitDialog() {
  submitForm.value = { taskId: null, reason: '' }
  submitDialogVisible.value = true
  try {
    const res = await getMyTasksApi({ page: 1, size: 100 })
    myCompletedTasks.value = (res.data?.records || []).filter(t => t.status === 3)
  } catch (e) { myCompletedTasks.value = [] }
}

function resetSubmitForm() {
  submitFormRef.value?.resetFields()
}

async function handleSubmitAppeal() {
  await submitFormRef.value.validate()
  submitLoading.value = true
  try {
    await addAppealApi(submitForm.value)
    ElMessage.success('申诉提交成功')
    submitDialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

// 处理申诉
const handleDialogVisible = ref(false)
const handleFormRef = ref()
const handleLoading = ref(false)
const handleForm = ref({ status: 3, reply: '' })
const currentAppealId = ref(null)
const currentAppeal = ref({})

const handleRules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  reply: [{ required: true, message: '请输入回复意见', trigger: 'blur' }]
}

function openHandleDialog(row) {
  currentAppealId.value = row.id
  currentAppeal.value = row
  handleForm.value = { status: 3, reply: '' }
  handleDialogVisible.value = true
}

function resetHandleForm() {
  handleFormRef.value?.resetFields()
}

async function handleSubmitHandle() {
  await handleFormRef.value.validate()
  handleLoading.value = true
  try {
    await handleAppealApi(currentAppealId.value, handleForm.value)
    ElMessage.success('处理成功')
    handleDialogVisible.value = false
    loadData()
  } finally { handleLoading.value = false }
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
.mb-16 {
  margin-bottom: 16px;
}
</style>
