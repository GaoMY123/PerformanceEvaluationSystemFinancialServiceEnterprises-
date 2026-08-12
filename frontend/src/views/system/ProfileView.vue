<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 左侧：头像卡片 -->
      <el-col :span="8">
        <el-card shadow="never" class="avatar-card">
          <div class="avatar-area">
            <el-upload
              class="avatar-uploader"
              action=""
              :show-file-list="false"
              :auto-upload="false"
              :on-change="handleAvatarChange"
              accept="image/*"
            >
              <el-avatar :size="120" :src="avatarUrl" class="avatar-img">
                <el-icon :size="48"><User /></el-icon>
              </el-avatar>
              <div class="avatar-overlay"><el-icon :size="24"><Camera /></el-icon></div>
            </el-upload>
            <h3 class="user-name">{{ form.realName || form.username }}</h3>
            <el-tag :type="roleTagType" effect="dark" size="small">{{ roleMap[form.role] || form.role }}</el-tag>
            <div class="user-dept" v-if="departmentName">
              <el-icon><OfficeBuilding /></el-icon> {{ departmentName }}
            </div>
            <div class="user-status">
              <el-tag :type="form.status === 1 ? 'success' : 'danger'" size="small" effect="light">
                {{ form.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
          </div>
          <div class="avatar-actions">
            <el-button size="small" @click="pwdDialogVisible = true">
              <el-icon><Lock /></el-icon> 修改密码
            </el-button>
          </div>
          <div class="meta-info">
            <div class="meta-item">
              <span class="meta-label">注册时间</span>
              <span class="meta-value">{{ form.createTime || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">最近更新</span>
              <span class="meta-value">{{ form.updateTime || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：信息编辑 -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
              <el-button type="primary" :loading="saving" @click="handleSave">
                <el-icon><Check /></el-icon> 保存修改
              </el-button>
            </div>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="profile-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input :model-value="form.username" disabled prefix-icon="User" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="角色">
                  <el-input :model-value="roleMap[form.role] || form.role" disabled />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="form.realName" placeholder="请输入真实姓名" prefix-icon="Postcard" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属部门">
                  <el-input :model-value="departmentName" disabled prefix-icon="OfficeBuilding" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" prefix-icon="Message" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号" prefix-icon="Phone" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="pwdDialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { User, Camera, Lock, Check, OfficeBuilding, Postcard, Message, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateProfileApi, changePasswordApi } from '@/api/auth'
import { uploadAvatarApi } from '@/api/user'
import { getAllDepartmentsApi } from '@/api/department'

const userStore = useUserStore()
const roleMap = { ADMIN: '系统管理员', HR: 'HR专员', MANAGER: '部门经理', EMPLOYEE: '员工' }
const roleTagType = computed(() => {
  const m = { ADMIN: 'danger', HR: 'warning', MANAGER: '', EMPLOYEE: 'info' }
  return m[form.value.role] || 'info'
})

const formRef = ref()
const saving = ref(false)
const departments = ref([])
const departmentName = computed(() => {
  if (!form.value.departmentId) return ''
  const dept = departments.value.find(d => d.id === form.value.departmentId)
  return dept ? dept.name : ''
})

const form = ref({
  username: '', role: '', realName: '', email: '', phone: '',
  avatar: '', departmentId: null, status: 1, createTime: '', updateTime: ''
})

const avatarUrl = computed(() => {
  if (form.value.avatar) {
    return form.value.avatar.startsWith('http') ? form.value.avatar : `/api${form.value.avatar}`
  }
  return ''
})

const rules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

// 密码修改
const pwdDialogVisible = ref(false)
const pwdFormRef = ref()
const pwdLoading = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdValidate = (rule, value, callback) => {
  if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: pwdValidate, trigger: 'blur' }
  ]
}

onMounted(async () => {
  // 加载部门列表
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }

  // 填充表单
  const u = userStore.userInfo
  if (u) {
    form.value = {
      username: u.username || '',
      role: u.role || '',
      realName: u.realName || '',
      email: u.email || '',
      phone: u.phone || '',
      avatar: u.avatar || '',
      departmentId: u.departmentId || null,
      status: u.status ?? 1,
      createTime: u.createTime || '',
      updateTime: u.updateTime || ''
    }
  }
})

async function handleAvatarChange(file) {
  if (!file.raw) return
  if (file.raw.size > 2 * 1024 * 1024) {
    ElMessage.warning('头像文件大小不能超过2MB')
    return
  }
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const res = await uploadAvatarApi(formData)
    form.value.avatar = res.data
    await updateProfileApi({ realName: form.value.realName, email: form.value.email, phone: form.value.phone, avatar: form.value.avatar })
    await userStore.fetchUserInfo()
    ElMessage.success('头像更新成功')
  } catch (e) { /* handled by interceptor */ }
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    await updateProfileApi({
      realName: form.value.realName,
      email: form.value.email,
      phone: form.value.phone,
      avatar: form.value.avatar
    })
    ElMessage.success('保存成功')
    await userStore.fetchUserInfo()
  } finally {
    saving.value = false
  }
}

async function handleChangePassword() {
  await pwdFormRef.value.validate()
  pwdLoading.value = true
  try {
    await changePasswordApi({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    pwdDialogVisible.value = false
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    // 延迟退出让用户看到提示
    setTimeout(() => {
      userStore.logout()
      window.location.href = '/login'
    }, 1500)
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.profile-page { padding: 0; }

.avatar-card { text-align: center; }
.avatar-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0 12px;
}
.avatar-uploader {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.avatar-uploader :deep(.el-upload) {
  position: relative;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-img {
  border: 3px solid #e4e7ed;
  background: #f0f2f5;
  transition: border-color 0.3s;
}
.avatar-img:hover {
  border-color: #409eff;
}
.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 36px;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}
.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.user-name {
  margin: 12px 0 6px;
  font-size: 18px;
  color: #303133;
}
.user-dept {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}
.user-status { margin-top: 6px; }

.avatar-actions {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.meta-info {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.meta-item {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 13px;
}
.meta-label { color: #909399; }
.meta-value { color: #606266; }

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.profile-form {
  padding: 10px 20px 0;
}
</style>
