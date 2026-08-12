<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="目标名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" placeholder="全部" clearable style="width: 120px;">
            <el-option label="公司级" value="COMPANY" />
            <el-option label="部门级" value="DEPARTMENT" />
            <el-option label="个人级" value="PERSONAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" v-if="isAdminOrHR">
          <el-select v-model="query.departmentId" placeholder="全部" clearable style="width: 140px;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年度">
          <el-input-number v-model="query.year" :min="2020" :max="2030" controls-position="right" style="width: 120px;" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openDialog(null)" v-if="canManage">新增目标</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true" row-key="id">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="title" label="目标名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="level" label="级别" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.level)" size="small" effect="light">{{ levelMap[row.level] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="部门" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.departmentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="责任人" width="100" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.userName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="year" label="年度" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small" effect="light">
              {{ statusMap[row.status] }}
            </el-tag>
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
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewKpi(row)" v-if="canManage">关联KPI</el-button>
            <el-button link type="warning" @click="openDialog(row)" v-if="canManage">编辑</el-button>
            <el-popconfirm title="确定删除该目标？" @confirm="handleDelete(row)" v-if="canManage">
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

    <!-- 新增/编辑目标 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑目标' : '新增目标'" width="580px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="目标名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入目标名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入目标描述" />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select v-model="form.level" placeholder="请选择级别" style="width: 100%;" @change="onLevelChange">
            <el-option label="公司级" value="COMPANY" v-if="isAdminOrHR" />
            <el-option label="部门级" value="DEPARTMENT" />
            <el-option label="个人级" value="PERSONAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级目标">
          <el-select v-model="form.parentId" placeholder="无（顶级目标）" clearable style="width: 100%;">
            <el-option v-for="g in parentGoals" :key="g.id" :label="g.title" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" v-if="form.level === 'DEPARTMENT' || form.level === 'PERSONAL'" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择部门" clearable style="width: 100%;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="责任人" v-if="form.level === 'PERSONAL'">
          <el-select v-model="form.userId" placeholder="请选择责任人" clearable filterable style="width: 100%;">
            <el-option v-for="u in users" :key="u.id" :label="u.realName + '（' + u.username + '）'" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="年度" prop="year">
          <el-input-number v-model="form.year" :min="2020" :max="2030" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">草稿</el-radio>
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="2">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 关联KPI弹窗 -->
    <el-dialog v-model="kpiDialogVisible" title="关联KPI指标" width="700px" :close-on-click-modal="false">
      <el-table :data="goalKpiList" border size="small" v-loading="kpiLoading">
        <el-table-column prop="kpiName" label="KPI指标" min-width="160" show-overflow-tooltip />
        <el-table-column prop="targetValue" label="目标值" width="100" align="center" />
        <el-table-column prop="weight" label="权重(%)" width="100" align="center" />
        <el-table-column label="操作" width="80" align="center" v-if="canManage">
          <template #default="{ row }">
            <el-popconfirm title="确定取消关联？" @confirm="handleUnbindKpi(row)">
              <template #reference>
                <el-button link type="danger" size="small">移除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px;" v-if="canManage">
        <el-divider content-position="left">添加关联</el-divider>
        <el-form :inline="true" :model="kpiBindForm" size="small">
          <el-form-item label="KPI">
            <el-select v-model="kpiBindForm.kpiId" placeholder="选择指标" filterable style="width: 200px;">
              <el-option v-for="k in allKpis" :key="k.id" :label="k.name" :value="k.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="目标值">
            <el-input-number v-model="kpiBindForm.targetValue" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="权重%">
            <el-input-number v-model="kpiBindForm.weight" :min="0" :max="100" :precision="2" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" @click="handleBindKpi">添加</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getGoalListApi, addGoalApi, updateGoalApi, deleteGoalApi, bindGoalKpiApi, getGoalKpiListApi, unbindGoalKpiApi } from '@/api/goal'
import { getAllDepartmentsApi } from '@/api/department'
import { getAllKpiApi } from '@/api/kpi'
import { getUserListApi } from '@/api/user'

const userStore = useUserStore()
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')
const canManage = computed(() => ['ADMIN', 'HR', 'MANAGER'].includes(userStore.role))

const levelMap = { COMPANY: '公司级', DEPARTMENT: '部门级', PERSONAL: '个人级' }
function levelTagType(level) {
  return { COMPANY: 'danger', DEPARTMENT: 'warning', PERSONAL: '' }[level] || 'info'
}

const statusMap = { 0: '草稿', 1: '已发布', 2: '已完成' }
function statusTagType(status) {
  return { 0: 'info', 1: 'success', 2: '' }[status] || 'info'
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', level: '', departmentId: null, year: 2026, status: null, page: 1, size: 10 })
const departments = ref([])
const allGoals = ref([])
const allKpis = ref([])
const users = ref([])

onMounted(() => {
  // MANAGER自动过滤本部门
  if (isManager.value && userStore.userInfo?.departmentId) {
    query.value.departmentId = userStore.userInfo.departmentId
  }
  loadData()
  loadDepartments()
  loadAllKpis()
  if (isAdminOrHR.value) {
    loadUsers()
  }
})

async function loadData() {
  loading.value = true
  try {
    const res = await getGoalListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
    allGoals.value = tableData.value
  } finally { loading.value = false }
}

async function loadDepartments() {
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadAllKpis() {
  try {
    const res = await getAllKpiApi()
    allKpis.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadUsers() {
  try {
    const res = await getUserListApi({ page: 1, size: 200 })
    users.value = res.data?.records || []
  } catch (e) { /* ignore */ }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', level: '', departmentId: null, year: 2026, status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 上级目标列表（排除自身）
const parentGoals = computed(() => {
  if (!isEdit.value) return allGoals.value
  return allGoals.value.filter(g => g.id !== form.value.id)
})

// 目标弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const rules = {
  title: [{ required: true, message: '请输入目标名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择级别', trigger: 'change' }],
  year: [{ required: true, message: '请输入年度', trigger: 'blur' }]
}

function openDialog(row) {
  isEdit.value = !!row
  const defaultLevel = isManager.value ? 'DEPARTMENT' : 'COMPANY'
  const defaultDeptId = isManager.value ? userStore.userInfo?.departmentId : null
  form.value = row
    ? { ...row }
    : { title: '', description: '', level: defaultLevel, parentId: 0, departmentId: defaultDeptId, userId: null, year: 2026, status: 0 }
  dialogVisible.value = true
}

function onLevelChange() {
  if (form.value.level === 'COMPANY') {
    form.value.departmentId = null
    form.value.userId = null
  } else if (form.value.level === 'DEPARTMENT') {
    form.value.userId = null
  }
}

function resetForm() {
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateGoalApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addGoalApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await deleteGoalApi(row.id)
  ElMessage.success('删除成功')
  loadData()
}

// KPI关联
const kpiDialogVisible = ref(false)
const kpiLoading = ref(false)
const goalKpiList = ref([])
const currentGoalId = ref(null)
const kpiBindForm = ref({ kpiId: null, targetValue: 100, weight: 50 })

async function viewKpi(row) {
  currentGoalId.value = row.id
  kpiDialogVisible.value = true
  kpiLoading.value = true
  try {
    const res = await getGoalKpiListApi(row.id)
    goalKpiList.value = res.data || []
  } catch (e) { goalKpiList.value = [] }
  finally { kpiLoading.value = false }
}

async function handleBindKpi() {
  if (!kpiBindForm.value.kpiId) {
    ElMessage.warning('请选择KPI指标')
    return
  }
  await bindGoalKpiApi({ goalId: currentGoalId.value, ...kpiBindForm.value })
  ElMessage.success('关联成功')
  kpiBindForm.value = { kpiId: null, targetValue: 100, weight: 50 }
  viewKpi({ id: currentGoalId.value })
}

async function handleUnbindKpi(row) {
  await unbindGoalKpiApi(row.id)
  ElMessage.success('已取消关联')
  viewKpi({ id: currentGoalId.value })
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
