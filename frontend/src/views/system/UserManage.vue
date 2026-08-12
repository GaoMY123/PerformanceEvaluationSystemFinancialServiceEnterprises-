<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="用户名/姓名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable style="width: 120px;">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="HR" value="HR" />
            <el-option label="经理" value="MANAGER" />
            <el-option label="员工" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.departmentId" placeholder="全部" clearable style="width: 160px;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
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
          <el-button :icon="Plus" type="success" @click="openDialog(null)">新增用户</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="头像" width="70" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar || undefined">
              {{ row.avatar ? '' : getFirstLetter(row.realName || row.username) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="realName" label="姓名" width="100" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" show-overflow-tooltip />
        <el-table-column label="部门" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getDeptName(row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" size="small">{{ roleMap[row.role] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="handleStatusChange(row)"
              :disabled="row.role === 'ADMIN'"
            />
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
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="openResetPwdDialog(row)">重置密码</el-button>
            <el-popconfirm
              title="确定删除该用户？"
              @confirm="handleDelete(row)"
              v-if="row.role !== 'ADMIN'"
            >
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="520px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="头像">
          <AvatarUpload v-model="form.avatar" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择部门" clearable style="width: 100%;">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="HR" value="HR" />
            <el-option label="经理" value="MANAGER" />
            <el-option label="员工" value="EMPLOYEE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="420px" :close-on-click-modal="false">
      <el-form ref="resetPwdFormRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="80px">
        <el-form-item label="用户">
          <span>{{ resetPwdRow?.realName }}（{{ resetPwdRow?.username }}）</span>
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdForm.password" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" show-password placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" :loading="resetPwdLoading" @click="handleResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getUserListApi, addUserApi, updateUserApi, resetPasswordApi, changeUserStatusApi, deleteUserApi } from '@/api/user'
import { getAllDepartmentsApi } from '@/api/department'
import AvatarUpload from '@/components/AvatarUpload.vue'

const roleMap = { ADMIN: '管理员', HR: 'HR', MANAGER: '经理', EMPLOYEE: '员工' }
function roleTagType(role) {
  const m = { ADMIN: 'danger', HR: 'warning', MANAGER: '', EMPLOYEE: 'info' }
  return m[role] || 'info'
}

function getFirstLetter(name) {
  if (!name) return ''
  return name.charAt(0).toUpperCase()
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

function getDeptName(departmentId) {
  if (!departmentId) return '-'
  const dept = departments.value.find(d => d.id === departmentId)
  return dept ? dept.name : '-'
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', role: '', departmentId: null, status: null, page: 1, size: 10 })
const departments = ref([])

onMounted(() => { loadData(); loadDepartments() })

async function loadData() {
  loading.value = true
  try {
    const res = await getUserListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

async function loadDepartments() {
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', role: '', departmentId: null, status: null, page: 1, size: 10 }
  loadData()
}

function handleSizeChange() {
  query.value.page = 1
  loadData()
}

// 新增/编辑弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function openDialog(row) {
  isEdit.value = !!row
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { username: '', realName: '', email: '', phone: '', departmentId: null, role: 'EMPLOYEE', avatar: '' }
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
      await updateUserApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addUserApi(form.value)
      ElMessage.success('新增成功，默认密码: 123456')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

// 重置密码弹窗
const resetPwdVisible = ref(false)
const resetPwdRow = ref(null)
const resetPwdFormRef = ref()
const resetPwdLoading = ref(false)
const resetPwdForm = ref({ password: '', confirmPassword: '' })

const resetPwdRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== resetPwdForm.value.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function openResetPwdDialog(row) {
  resetPwdRow.value = row
  resetPwdForm.value = { password: '', confirmPassword: '' }
  resetPwdVisible.value = true
}

async function handleResetPwd() {
  await resetPwdFormRef.value.validate()
  resetPwdLoading.value = true
  try {
    await resetPasswordApi(resetPwdRow.value.id, resetPwdForm.value.password)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } finally { resetPwdLoading.value = false }
}

// 状态切换
async function handleStatusChange(row) {
  if (row.role === 'ADMIN') {
    ElMessage.warning('不能禁用管理员账号')
    return
  }
  const newStatus = row.status === 1 ? 0 : 1
  await changeUserStatusApi(row.id, newStatus)
  ElMessage.success('状态修改成功')
  loadData()
}

// 删除
async function handleDelete(row) {
  if (row.role === 'ADMIN') {
    ElMessage.warning('不能删除管理员账号')
    return
  }
  await deleteUserApi(row.id)
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
