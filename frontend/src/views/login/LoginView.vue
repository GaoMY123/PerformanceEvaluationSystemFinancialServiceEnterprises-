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
            <span>多维度绩效评估体系</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>智能化考核流程管理</span>
          </div>
          <div class="feature-item">
            <div class="feature-dot"></div>
            <span>数据驱动的决策支持</span>
          </div>
        </div>
        <div class="left-decoration"></div>
      </div>
    </div>

    <!-- 右侧表单 -->
    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码"
              :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
              登 录
            </el-button>
          </el-form-item>
          <div class="login-footer">
            <router-link to="/register">注册账号</router-link>
            <router-link to="/findPassword">忘记密码</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = ref({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.login(form.value)
    ElMessage.success('登录成功')
    router.push('/home')
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

/* 左侧装饰面板 */
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

/* 右侧表单 */
.login-right {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafbff;
  position: relative;
}

.form-wrapper {
  width: 380px;
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
  display: flex;
  justify-content: space-between;
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

/* 响应式 */
@media (max-width: 900px) {
  .login-left { display: none; }
  .login-right { width: 100%; }
}
</style>
