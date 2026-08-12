import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/RegisterView.vue'),
    meta: { title: '注册', noAuth: true }
  },
  {
    path: '/findPassword',
    name: 'FindPassword',
    component: () => import('@/views/login/FindPasswordView.vue'),
    meta: { title: '找回密码', noAuth: true }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/HomeView.vue'),
        meta: { title: '首页' }
      },
      // ---- 系统管理 ----
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/system/DepartmentManage.vue'),
        meta: { title: '部门管理', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'announcement',
        name: 'Announcement',
        component: () => import('@/views/system/AnnouncementManage.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'log',
        name: 'OperationLog',
        component: () => import('@/views/system/OperationLog.vue'),
        meta: { title: '操作日志', roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/system/ProfileView.vue'),
        meta: { title: '个人信息' }
      },
      // ---- 绩效目标 ----
      {
        path: 'kpi',
        name: 'KpiIndicator',
        component: () => import('@/views/goal/KpiManage.vue'),
        meta: { title: 'KPI指标库', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'goal',
        name: 'Goal',
        component: () => import('@/views/goal/GoalManage.vue'),
        meta: { title: '绩效目标管理' }
      },
      // ---- 绩效考核 ----
      {
        path: 'plan',
        name: 'EvaluationPlan',
        component: () => import('@/views/evaluation/PlanManage.vue'),
        meta: { title: '考核方案', roles: ['ADMIN', 'HR'] }
      },
      {
        path: 'task',
        name: 'EvaluationTask',
        component: () => import('@/views/evaluation/TaskManage.vue'),
        meta: { title: '考核任务' }
      },
      // ---- 绩效结果 ----
      {
        path: 'salary',
        name: 'Salary',
        component: () => import('@/views/result/SalaryManage.vue'),
        meta: { title: '薪酬调整', roles: ['ADMIN', 'HR', 'MANAGER'] }
      },
      {
        path: 'idp',
        name: 'DevelopmentPlan',
        component: () => import('@/views/result/IdpManage.vue'),
        meta: { title: '个人发展计划' }
      },
      {
        path: 'appeal',
        name: 'Appeal',
        component: () => import('@/views/result/AppealManage.vue'),
        meta: { title: '绩效申诉' }
      },
      // ---- 分析 ----
      {
        path: 'analysis',
        name: 'Analysis',
        component: () => import('@/views/report/AnalysisView.vue'),
        meta: { title: '多维度分析', roles: ['ADMIN', 'HR', 'MANAGER'] }
      },
      // ---- 报表 ----
      {
        path: 'report',
        name: 'Report',
        component: () => import('@/views/report/ReportView.vue'),
        meta: { title: '报表导出', roles: ['ADMIN', 'HR', 'MANAGER'] }
      }
    ]
  },
  // 404
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || '绩效考核系统') + ' - 金融服务绩效考核系统'
  const userStore = useUserStore()

  // 不需要登录的页面
  if (to.meta.noAuth) {
    if (userStore.isLoggedIn && to.path === '/login') {
      return next('/home')
    }
    return next()
  }

  // 需要登录
  if (!userStore.isLoggedIn) {
    return next('/login')
  }

  // 角色权限校验
  if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
    return next('/home')
  }

  next()
})

export default router
