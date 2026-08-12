<template>
  <div class="login-page">
    <!-- 左侧装饰面板 -->
    <div class="login-left">
      <div class="left-content">
        <div class="brand-icon">
          <el-icon :size="48" color="#fff"><DataAnalysis /></el-icon>
        </div>
        <h1 class="brand-title">金融服务类企业<br/>绩效考核系统</h1>
        <p class="brand-subtitle">Financial Service Performance Evaluation System</p>
        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>公平公正的考核机制</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>全方位人才发展追踪</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>高效协同的工作平台</span>
          </div>
        </div>
        <div class="left-decoration"></div>
      </div>
    </div>

    <!-- 右侧表单 -->
    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>创建账号</h2>
          <p>填写以下信息完成注册</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="所属部门" prop="departmentId">
            <el-select v-model="form.departmentId" placeholder="请选择所属部门" style="width: 100%;">
              <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleRegister">
              注 册
            </el-button>
          </el-form-item>
          <div class="login-footer">
            <router-link to="/login">已有账号？去登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerApi } from '@/api/auth'
import { getAllDepartmentsApi } from '@/api/department'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const departments = ref([])
const form = ref({
  username: '', password: '', confirmPassword: '',
  realName: '', email: '', phone: '', departmentId: null
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.password) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
}

onMounted(async () => {
  try {
    const res = await getAllDepartmentsApi()
    departments.value = res.data || []
  } catch (e) { /* ignore */ }
})

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await registerApi(form.value)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1a237e 0%, #283593 30%, #3949ab 60%, #5c6bc0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.left-content {
  position: relative;
  z-index: 2;
  padding: 60px;
  color: #fff;
  max-width: 480px;
}

.brand-icon {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32px;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0 0 12px 0;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 48px 0;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #82b1ff;
  flex-shrink: 0;
}

.left-decoration {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
  bottom: -120px;
  right: -120px;
  z-index: 1;
}

.login-right {
  width: 560px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbff;
  overflow-y: auto;
}

.form-wrapper {
  width: 420px;
  padding: 40px 0;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1a237e;
  margin: 0 0 8px 0;
}

.form-header p {
  font-size: 14px;
  color: #9e9e9e;
  margin: 0;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  background: linear-gradient(135deg, #3949ab, #5c6bc0);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #283593, #3949ab);
}

.login-footer {
  text-align: center;
  font-size: 14px;
}

.login-footer a {
  color: #5c6bc0;
  text-decoration: none;
  transition: color 0.2s;
}

.login-footer a:hover {
  color: #1a237e;
}

@media (max-width: 900px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
