<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="部门名称" clearable @keyup.enter="handleSearch" />
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
          <el-button :icon="Plus" type="success" @click="openDialog(null)">新增部门</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="部门名称" width="160" show-overflow-tooltip />
        <el-table-column label="上级部门" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getParentName(row.parentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="managerName" label="部门经理" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.managerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small" effect="light">
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
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该部门？" @confirm="handleDelete(row)">
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="520px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-select v-model="form.parentId" placeholder="顶级部门" clearable style="width: 100%;">
            <el-option label="顶级部门" :value="0" />
            <el-option v-for="d in parentDepartments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门经理" prop="managerId">
          <el-select v-model="form.managerId" placeholder="请选择部门经理" clearable filterable style="width: 100%;">
            <el-option v-for="u in managers" :key="u.id" :label="u.realName + '（' + u.username + '）'" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%;" />
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
import { ref, computed, onMounted } from 'vue'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getDepartmentListApi, addDepartmentApi, updateDepartmentApi, deleteDepartmentApi, getAllDepartmentsApi } from '@/api/department'
import { getUserListApi } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', status: null, page: 1, size: 10 })
const allDepartments = ref([])
const managers = ref([])

onMounted(() => { loadData(); loadDepartments(); loadManagers() })

async function loadData() {
  loading.value = true
  try {
    const res = await getDepartmentListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function loadDepartments() {
  try {
    const res = await getAllDepartmentsApi()
    allDepartments.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadManagers() {
  try {
    const res = await getUserListApi({ page: 1, size: 200, role: 'MANAGER' })
    managers.value = res.data?.records || []
  } catch (e) { /* ignore */ }
}

function getParentName(parentId) {
  if (!parentId || parentId === 0) return '顶级部门'
  const dept = allDepartments.value.find(d => d.id === parentId)
  return dept ? dept.name : '-'
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 上级部门列表（排除自身，避免循环）
const parentDepartments = computed(() => {
  if (!isEdit.value) return allDepartments.value
  return allDepartments.value.filter(d => d.id !== form.value.id)
})

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const rules = {
  name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
}

function openDialog(row) {
  isEdit.value = !!row
  form.value = row
    ? { ...row }
    : { name: '', parentId: 0, managerId: null, description: '', sortOrder: 0, status: 1 }
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
      await updateDepartmentApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addDepartmentApi(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
    loadDepartments()
  } finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await deleteDepartmentApi(row.id)
  ElMessage.success('删除成功')
  loadData()
  loadDepartments()
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
