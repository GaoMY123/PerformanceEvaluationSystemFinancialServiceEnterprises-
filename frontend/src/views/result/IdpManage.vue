<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 角色标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的计划" name="my" />
        <el-tab-pane label="部门计划" name="department" v-if="isManager" />
        <el-tab-pane label="全部计划" name="all" v-if="isAdminOrHR" />
      </el-tabs>

      <!-- 筛选栏（全部计划tab） -->
      <el-form :inline="true" :model="query" class="search-form" v-if="activeTab === 'all'">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="员工姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="考核方案">
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
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="草稿" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 筛选栏（部门计划tab） -->
      <el-form :inline="true" :model="deptQuery" class="search-form" v-if="activeTab === 'department'">
        <el-form-item label="状态">
          <el-select v-model="deptQuery.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="草稿" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleDeptReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div style="margin-bottom: 12px;" v-if="isAdminOrHR">
        <el-button type="success" :icon="Plus" @click="openDialog(null)">新增计划</el-button>
        <el-button type="warning" @click="handleGenerate" v-if="activeTab === 'all'">按方案生成</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="planName" label="考核方案" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.planName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="userName" label="员工" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="departmentName" label="部门" width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ row.departmentName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="strengths" label="优势" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.strengths || '-' }}</template>
        </el-table-column>
        <el-table-column prop="weaknesses" label="不足" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.weaknesses || '-' }}</template>
        </el-table-column>
        <el-table-column prop="trainingSuggestion" label="培训建议" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.trainingSuggestion || '-' }}</template>
        </el-table-column>
        <el-table-column prop="developmentGoal" label="发展目标" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.developmentGoal || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="idpStatusTagType(row.status)" size="small" effect="light">{{ idpStatusMap[row.status] }}</el-tag>
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
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
            <el-button link type="warning" @click="openDialog(row)" v-if="isAdminOrHR && row.status < 3">编辑</el-button>
            <el-button link type="success" @click="handleChangeStatus(row, row.status + 1)"
              v-if="isAdminOrHR && row.status < 3">{{ nextStatusLabel(row.status) }}</el-button>
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
    <el-dialog v-model="detailVisible" title="个人发展计划详情" width="700px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="考核方案">{{ currentRow.planName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="员工">{{ currentRow.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentRow.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="idpStatusTagType(currentRow.status)" size="small" effect="light">{{ idpStatusMap[currentRow.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优势" :span="2">{{ currentRow.strengths || '-' }}</el-descriptions-item>
        <el-descriptions-item label="不足" :span="2">{{ currentRow.weaknesses || '-' }}</el-descriptions-item>
        <el-descriptions-item label="培训建议" :span="2">{{ currentRow.trainingSuggestion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发展目标" :span="2">{{ currentRow.developmentGoal || '-' }}</el-descriptions-item>
        <el-descriptions-item label="行动计划" :span="2">{{ currentRow.actionPlan || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑发展计划' : '新增发展计划'" width="640px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="idpRules" label-width="90px">
        <el-form-item label="考核方案" prop="planId" v-if="!isEdit">
          <el-select v-model="form.planId" placeholder="请选择方案" style="width: 100%;">
            <el-option v-for="p in plans" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="优势">
          <el-input v-model="form.strengths" type="textarea" :rows="2" placeholder="请输入优势分析" />
        </el-form-item>
        <el-form-item label="不足">
          <el-input v-model="form.weaknesses" type="textarea" :rows="2" placeholder="请输入待改进项" />
        </el-form-item>
        <el-form-item label="培训建议">
          <el-input v-model="form.trainingSuggestion" type="textarea" :rows="2" placeholder="请输入培训建议" />
        </el-form-item>
        <el-form-item label="发展目标" prop="developmentGoal">
          <el-input v-model="form.developmentGoal" type="textarea" :rows="2" placeholder="请输入发展目标" />
        </el-form-item>
        <el-form-item label="行动计划">
          <el-input v-model="form.actionPlan" type="textarea" :rows="3" placeholder="请输入行动计划" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 生成方案选择弹窗 -->
    <el-dialog v-model="generateVisible" title="按方案生成发展计划" width="400px">
      <el-form label-width="90px">
        <el-form-item label="考核方案">
          <el-select v-model="generatePlanId" placeholder="请选择方案" style="width: 100%;">
            <el-option v-for="p in plans" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" :loading="generateLoading" @click="doGenerate">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getIdpListApi, getMyIdpApi, getDepartmentIdpApi, addIdpApi, updateIdpApi, changeIdpStatusApi, generateIdpApi } from '@/api/idp'
import { getPlanListApi } from '@/api/plan'
import { getAllDepartmentsApi } from '@/api/department'

const userStore = useUserStore()
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')

const idpStatusMap = { 0: '草稿', 1: '已确认', 2: '执行中', 3: '已完成' }
function idpStatusTagType(s) { return { 0: 'info', 1: '', 2: 'warning', 3: 'success' }[s] || 'info' }
function nextStatusLabel(s) { return { 0: '确认', 1: '开始执行', 2: '完成' }[s] || '' }

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', planId: null, departmentId: null, status: null, page: 1, size: 10 })
const deptQuery = ref({ status: null, page: 1, size: 10 })
const activeTab = ref('my')
const plans = ref([])
const departments = ref([])

onMounted(async () => {
  try {
    const [planRes, deptRes] = await Promise.all([
      getPlanListApi({ page: 1, size: 100 }),
      getAllDepartmentsApi()
    ])
    plans.value = planRes.data?.records || []
    departments.value = deptRes.data || []
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
      res = await getMyIdpApi(query.value)
    } else if (activeTab.value === 'department') {
      res = await getDepartmentIdpApi(deptQuery.value)
    } else {
      res = await getIdpListApi(query.value)
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
  query.value = { keyword: '', planId: null, departmentId: null, status: null, page: 1, size: 10 }
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

// 状态变更
async function handleChangeStatus(row, newStatus) {
  const label = idpStatusMap[newStatus]
  await ElMessageBox.confirm(`确认将状态改为"${label}"？`, '状态变更', { type: 'warning' })
  await changeIdpStatusApi(row.id, newStatus)
  ElMessage.success('状态已更新')
  loadData()
}

// 生成
const generateVisible = ref(false)
const generatePlanId = ref(null)
const generateLoading = ref(false)

function handleGenerate() {
  generatePlanId.value = null
  generateVisible.value = true
}

async function doGenerate() {
  if (!generatePlanId.value) {
    ElMessage.warning('请选择考核方案')
    return
  }
  generateLoading.value = true
  try {
    await generateIdpApi(generatePlanId.value)
    ElMessage.success('发展计划已生成')
    generateVisible.value = false
    loadData()
  } finally { generateLoading.value = false }
}

// 新增/编辑弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const idpRules = {
  developmentGoal: [{ required: true, message: '请输入发展目标', trigger: 'blur' }],
  planId: [{ required: true, message: '请选择考核方案', trigger: 'change' }]
}

function openDialog(row) {
  isEdit.value = !!row
  form.value = row ? { ...row } : {
    planId: null, strengths: '', weaknesses: '', trainingSuggestion: '',
    developmentGoal: '', actionPlan: '', status: 0
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
      await updateIdpApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addIdpApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
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
</style>
