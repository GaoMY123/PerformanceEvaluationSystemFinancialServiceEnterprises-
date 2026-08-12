<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏（ADMIN/HR） -->
      <el-form :inline="true" :model="query" class="search-form" v-if="isAdminOrHR">
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
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleGenerate" v-if="query.planId">生成调整建议</el-button>
        </el-form-item>
      </el-form>

      <!-- 搜索栏（MANAGER） -->
      <el-form :inline="true" :model="deptQuery" class="search-form" v-if="isManager">
        <el-form-item label="状态">
          <el-select v-model="deptQuery.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="待审批" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleDeptReset">重置</el-button>
        </el-form-item>
      </el-form>

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
        <el-table-column prop="grade" label="绩效等级" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.grade" :type="gradeTagType(row.grade)" size="small" effect="light">{{ row.grade }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="考核得分" width="90" align="center">
          <template #default="{ row }">
            <span :class="scoreClass(row.finalScore)">{{ row.finalScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="baseSalary" label="基本薪资" width="110" align="right">
          <template #default="{ row }">{{ formatMoney(row.baseSalary) }}</template>
        </el-table-column>
        <el-table-column prop="adjustmentRate" label="调整比例(%)" width="110" align="center" />
        <el-table-column prop="bonusAmount" label="奖金" width="110" align="right">
          <template #default="{ row }">{{ formatMoney(row.bonusAmount) }}</template>
        </el-table-column>
        <el-table-column prop="suggestion" label="建议" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="salaryStatusTagType(row.status)" size="small" effect="light">{{ salaryStatusMap[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">{{ formatTime(row.updateTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <template v-if="isAdminOrHR">
              <el-button link type="warning" @click="openEditDialog(row)" v-if="row.status === 0">编辑</el-button>
              <template v-if="row.status === 0">
                <el-button link type="success" @click="handleApprove(row, 1)">通过</el-button>
                <el-button link type="danger" @click="handleApprove(row, 2)">驳回</el-button>
              </template>
            </template>
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
    <el-dialog v-model="detailVisible" title="薪酬调整详情" width="650px">
      <el-descriptions :column="2" border size="small">
        <el-descriptions-item label="考核方案">{{ currentRow.planName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="员工">{{ currentRow.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentRow.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="绩效等级">
          <el-tag v-if="currentRow.grade" :type="gradeTagType(currentRow.grade)" size="small" effect="light">{{ currentRow.grade }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="考核得分">
          <span :class="scoreClass(currentRow.finalScore)">{{ currentRow.finalScore ?? '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="基本薪资">{{ formatMoney(currentRow.baseSalary) }}</el-descriptions-item>
        <el-descriptions-item label="调整比例(%)">{{ currentRow.adjustmentRate ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="奖金">{{ formatMoney(currentRow.bonusAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="salaryStatusTagType(currentRow.status)" size="small" effect="light">{{ salaryStatusMap[currentRow.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="建议" :span="2">{{ currentRow.suggestion || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑薪酬调整" width="560px" :close-on-click-modal="false" @close="resetEditForm">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="基本薪资" prop="baseSalary">
          <el-input-number v-model="editForm.baseSalary" :min="0" :precision="2" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="调整比例(%)" prop="adjustmentRate">
          <el-input-number v-model="editForm.adjustmentRate" :min="0" :max="100" :precision="2" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="奖金" prop="bonusAmount">
          <el-input-number v-model="editForm.bonusAmount" :min="0" :precision="2" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="建议">
          <el-input v-model="editForm.suggestion" type="textarea" :rows="3" placeholder="请输入调整建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getSalaryListApi, getDepartmentSalaryApi, generateSalaryApi, updateSalaryApi, approveSalaryApi } from '@/api/salary'
import { getPlanListApi } from '@/api/plan'
import { getAllDepartmentsApi } from '@/api/department'

const userStore = useUserStore()
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')

const salaryStatusMap = { 0: '待审批', 1: '已通过', 2: '已驳回' }
function salaryStatusTagType(s) { return { 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info' }
function gradeTagType(g) { return { A: 'success', B: '', C: 'warning', D: 'danger' }[g] || 'info' }
function scoreClass(s) { return s >= 90 ? 'score-high' : s >= 75 ? 'score-mid' : s >= 60 ? 'score-low' : 'score-fail' }
function formatMoney(v) { return v != null ? '¥' + Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2 }) : '-' }
function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', planId: null, departmentId: null, status: null, page: 1, size: 10 })
const deptQuery = ref({ status: null, page: 1, size: 10 })
const plans = ref([])
const departments = ref([])

onMounted(async () => {
  if (isAdminOrHR.value) {
    try {
      const [planRes, deptRes] = await Promise.all([
        getPlanListApi({ page: 1, size: 100 }),
        getAllDepartmentsApi()
      ])
      plans.value = planRes.data?.records || []
      departments.value = deptRes.data || []
    } catch (e) { /* ignore */ }
  }
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    let res
    if (isManager.value) {
      res = await getDepartmentSalaryApi(deptQuery.value)
    } else {
      res = await getSalaryListApi(query.value)
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

async function handleGenerate() {
  await ElMessageBox.confirm('将根据考核结果自动生成薪酬调整建议，是否继续？', '提示', { type: 'warning' })
  await generateSalaryApi(query.value.planId)
  ElMessage.success('生成成功')
  loadData()
}

async function handleApprove(row, status) {
  const tip = status === 1 ? '确认通过该薪酬调整？' : '确认驳回该薪酬调整？'
  await ElMessageBox.confirm(tip, '审批', { type: 'warning' })
  await approveSalaryApi(row.id, status)
  ElMessage.success('操作成功')
  loadData()
}

// 详情
const detailVisible = ref(false)
const currentRow = ref({})

function openDetail(row) {
  currentRow.value = row
  detailVisible.value = true
}

// 编辑
const editVisible = ref(false)
const editFormRef = ref()
const editLoading = ref(false)
const editForm = ref({})

const editRules = {
  baseSalary: [{ required: true, message: '请输入基本薪资', trigger: 'blur' }],
  adjustmentRate: [{ required: true, message: '请输入调整比例', trigger: 'blur' }]
}

function openEditDialog(row) {
  editForm.value = { id: row.id, baseSalary: row.baseSalary, adjustmentRate: row.adjustmentRate, bonusAmount: row.bonusAmount, suggestion: row.suggestion }
  editVisible.value = true
}

function resetEditForm() {
  editFormRef.value?.resetFields()
}

async function handleEditSubmit() {
  await editFormRef.value.validate()
  editLoading.value = true
  try {
    await updateSalaryApi(editForm.value)
    ElMessage.success('更新成功')
    editVisible.value = false
    loadData()
  } finally { editLoading.value = false }
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
