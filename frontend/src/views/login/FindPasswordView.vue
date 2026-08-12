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
            <span>安全可靠的数据保护</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>便捷的身份验证流程</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>7×24小时系统支持</span>
          </div>
        </div>
        <div class="left-decoration"></div>
      </div>
    </div>

    <!-- 右侧表单 -->
    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>找回密码</h2>
          <p>通过用户名和邮箱验证身份</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" size="large">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入注册邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleFind">
              找回密码
            </el-button>
          </el-form-item>

          <!-- 找回成功后显示新密码 -->
          <el-alert v-if="newPassword" type="success" :closable="false" show-icon
            :title="'密码已重置，新密码为：' + newPassword"
            description="请牢记新密码并前往登录" style="margin-bottom: 16px;" />

          <div class="login-footer">
            <router-link to="/login">返回登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { findPasswordApi } from '@/api/auth'

const formRef = ref()
const loading = ref(false)
const newPassword = ref('')
const form = ref({ username: '', email: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

async function handleFind() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await findPasswordApi(form.value)
    newPassword.value = res.data
    ElMessage.success('密码重置成功')
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
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbff;
}

.form-wrapper {
  width: 400px;
}

.form-header {
  margin-bottom: 40px;
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
  margin-top: 16px;
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
