<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-area">
        <div class="logo-icon-wrap">
          <el-icon :size="22" color="#fff"><DataAnalysis /></el-icon>
        </div>
        <transition name="fade-text">
          <span v-show="!isCollapse" class="logo-title">金融服务类企业<br/>绩效考核系统</span>
        </transition>
      </div>
      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapse"
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.65)"
        active-text-color="#fff"
        class="aside-menu"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>系统首页</span>
        </el-menu-item>

        <!-- 系统管理 - 仅ADMIN -->
        <el-sub-menu index="system" v-if="userStore.role === 'ADMIN'">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/department">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/announcement">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          <el-menu-item index="/log" v-if="userStore.role === 'ADMIN'">
            <el-icon><Document /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 公告查看 - HR/MANAGER/EMPLOYEE -->
        <el-menu-item index="/announcement" v-if="userStore.role !== 'ADMIN'">
          <el-icon><Bell /></el-icon>
          <span>系统公告</span>
        </el-menu-item>

        <!-- 绩效目标 -->
        <el-sub-menu index="goal-mgr">
          <template #title>
            <el-icon><Aim /></el-icon>
            <span>绩效目标</span>
          </template>
          <el-menu-item index="/kpi" v-if="isAdminOrHR">
            <el-icon><TrendCharts /></el-icon>
            <span>KPI指标库</span>
          </el-menu-item>
          <el-menu-item index="/goal">
            <el-icon><Flag /></el-icon>
            <span>目标管理</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 绩效考核 -->
        <el-sub-menu index="evaluation">
          <template #title>
            <el-icon><EditPen /></el-icon>
            <span>绩效考核</span>
          </template>
          <el-menu-item index="/plan" v-if="isAdminOrHR">
            <el-icon><Notebook /></el-icon>
            <span>考核方案</span>
          </el-menu-item>
          <el-menu-item index="/task">
            <el-icon><List /></el-icon>
            <span>{{ isManager ? '部门考核' : '考核任务' }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 绩效结果 -->
        <el-sub-menu index="result">
          <template #title>
            <el-icon><Trophy /></el-icon>
            <span>绩效结果</span>
          </template>
          <el-menu-item index="/salary" v-if="isAdminOrHR">
            <el-icon><Money /></el-icon>
            <span>薪酬调整</span>
          </el-menu-item>
          <el-menu-item index="/salary" v-if="isManager">
            <el-icon><Money /></el-icon>
            <span>部门薪酬</span>
          </el-menu-item>
          <el-menu-item index="/idp">
            <el-icon><Reading /></el-icon>
            <span>{{ isManager ? '部门发展计划' : '发展计划' }}</span>
          </el-menu-item>
          <el-menu-item index="/appeal">
            <el-icon><ChatDotSquare /></el-icon>
            <span>{{ isManager ? '部门申诉处理' : '绩效申诉' }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 报表 - ADMIN/HR/MANAGER -->
        <el-menu-item index="/analysis" v-if="isAdminOrHR || isManager">
          <el-icon><TrendCharts /></el-icon>
          <span>多维度分析</span>
        </el-menu-item>
        <el-menu-item index="/report" v-if="isAdminOrHR || isManager">
          <el-icon><Printer /></el-icon>
          <span>报表导出</span>
        </el-menu-item>
      </el-menu>

      <!-- 底部折叠按钮 -->
      <div class="aside-bottom" @click="isCollapse = !isCollapse">
        <el-icon :size="18">
          <Fold v-if="!isCollapse" />
          <Expand v-else />
        </el-icon>
        <span v-show="!isCollapse" class="collapse-text">收起菜单</span>
      </div>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-container>
      <!-- 顶栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title && $route.path !== '/home'">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <div class="role-badge">
            <el-tag :type="roleTagType" size="small" effect="dark" round>{{ roleLabel }}</el-tag>
          </div>
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="34" :src="userStore.userInfo?.avatar || undefined"
                :style="{ background: '#3949ab', fontSize: '14px' }">
                {{ userStore.userInfo?.avatar ? '' : (userStore.userInfo?.realName || userStore.userInfo?.username || '').charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="user-name">{{ userStore.realName }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="slide-fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码弹窗 -->
  <el-dialog v-model="pwdDialogVisible" title="修改密码" width="420px" :close-on-click-modal="false">
    <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="pwdForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleChangePassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { changePasswordApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const currentRoute = computed(() => route.path)
const isAdminOrHR = computed(() => ['ADMIN', 'HR'].includes(userStore.role))
const isManager = computed(() => userStore.role === 'MANAGER')
const isEmployee = computed(() => userStore.role === 'EMPLOYEE')

const roleMap = { ADMIN: '管理员', HR: 'HR专员', MANAGER: '部门经理', EMPLOYEE: '员工' }
const roleLabel = computed(() => roleMap[userStore.role] || userStore.role)
const roleTagType = computed(() => ({
  ADMIN: 'danger', HR: 'warning', MANAGER: '', EMPLOYEE: 'info'
}[userStore.role] || 'info'))

// 顶栏下拉菜单
function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'password') {
    pwdDialogVisible.value = true
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}

// 修改密码
const pwdDialogVisible = ref(false)
const pwdFormRef = ref()
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.newPassword) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

async function handleChangePassword() {
  await pwdFormRef.value.validate()
  await changePasswordApi({
    oldPassword: pwdForm.value.oldPassword,
    newPassword: pwdForm.value.newPassword
  })
  ElMessage.success('密码修改成功，请重新登录')
  pwdDialogVisible.value = false
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* ===== 侧边栏 ===== */
.layout-aside {
  background: linear-gradient(180deg, #1a237e 0%, #283593 40%, #303f9f 100%);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  flex-shrink: 0;
}

.logo-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  letter-spacing: 1px;
}

.aside-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.aside-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 菜单项激活态 */
:deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12) !important;
  border-radius: 0 20px 20px 0;
  margin-right: 12px;
  color: #fff !important;
  font-weight: 500;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
}

/* 底部折叠按钮 */
.aside-bottom {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  transition: all 0.2s;
  flex-shrink: 0;
}

.aside-bottom:hover {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.05);
}

.collapse-text {
  font-size: 12px;
  white-space: nowrap;
}

/* ===== 顶栏 ===== */
.layout-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.role-badge {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #303133;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: #999;
}

/* ===== 主内容区 ===== */
.layout-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* ===== 动画 ===== */
.fade-text-enter-active,
.fade-text-leave-active {
  transition: opacity 0.2s ease;
}
.fade-text-enter-from,
.fade-text-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: all 0.25s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.15s ease-in;
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(12px);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-12px);
}
</style>
