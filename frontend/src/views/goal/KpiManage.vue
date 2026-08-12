<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="名称">
          <el-input v-model="query.keyword" placeholder="指标名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.category" placeholder="全部" clearable style="width: 120px;">
            <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px;">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openDialog(null)">新增指标</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="指标名称" width="160" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="categoryTagType(row.category)" size="small" effect="light">
              {{ categoryMap[row.category] || row.category }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="formula" label="计算公式" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.formula || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="70" align="center">
          <template #default="{ row }">
            {{ row.unit || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="targetValue" label="目标值" width="90" align="center" />
        <el-table-column prop="maxScore" label="满分" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '启用' : '禁用' }}
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
        <el-table-column label="操作" width="140" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该指标？" @confirm="handleDelete(row)">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑指标' : '新增指标'" width="580px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="指标名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入指标名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入指标描述" />
        </el-form-item>
        <el-form-item label="计算公式">
          <el-input v-model="form.formula" placeholder="请输入计算公式" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="如：%、分、元" style="width: 160px;" />
        </el-form-item>
        <el-form-item label="目标值" prop="targetValue">
          <el-input-number v-model="form.targetValue" :min="0" :precision="2" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="满分" prop="maxScore">
          <el-input-number v-model="form.maxScore" :min="0" :precision="2" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getKpiListApi, addKpiApi, updateKpiApi, deleteKpiApi } from '@/api/kpi'

const categories = [
  { label: '合规类', value: 'COMPLIANCE' },
  { label: '风控类', value: 'RISK' },
  { label: '财务类', value: 'FINANCIAL' },
  { label: '客户类', value: 'CUSTOMER' },
  { label: '运营类', value: 'OPERATION' }
]
const categoryMap = { COMPLIANCE: '合规类', RISK: '风控类', FINANCIAL: '财务类', CUSTOMER: '客户类', OPERATION: '运营类' }
function categoryTagType(category) {
  const m = { COMPLIANCE: 'warning', RISK: 'danger', FINANCIAL: '', CUSTOMER: 'success', OPERATION: 'info' }
  return m[category] || 'info'
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', category: '', status: null, page: 1, size: 10 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getKpiListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', category: '', status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  targetValue: [{ required: true, message: '请输入目标值', trigger: 'blur' }],
  maxScore: [{ required: true, message: '请输入满分', trigger: 'blur' }]
}

function openDialog(row) {
  isEdit.value = !!row
  form.value = row
    ? { ...row }
    : { name: '', category: '', description: '', formula: '', unit: '', targetValue: 100, maxScore: 100, status: 1 }
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
      await updateKpiApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addKpiApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await deleteKpiApi(row.id)
  ElMessage.success('删除成功')
  loadData()
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
