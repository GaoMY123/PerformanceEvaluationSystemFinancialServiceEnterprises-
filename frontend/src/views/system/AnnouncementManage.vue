<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="公告标题" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px;">
            <el-option label="已发布" :value="1" />
            <el-option label="草稿" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openDialog(null)" v-if="canManage">发布公告</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="publisherName" label="发布人" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.publisherName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ row.status === 1 ? '已发布' : '草稿' }}
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
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">查看</el-button>
            <template v-if="canManage">
              <el-button link type="warning" @click="openDialog(row)">编辑</el-button>
              <el-popconfirm title="确定删除该公告？" @confirm="handleDelete(row)">
                <template #reference>
                  <el-button link type="danger">删除</el-button>
                </template>
              </el-popconfirm>
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

    <!-- 查看详情 -->
    <el-dialog v-model="detailVisible" width="680px" :close-on-click-modal="false">
      <template #header>
        <div class="detail-header">
          <h3 class="detail-title">{{ detailData.title }}</h3>
          <div class="detail-meta">
            <el-tag :type="detailData.status === 1 ? 'success' : 'info'" size="small" effect="light">
              {{ detailData.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
            <span class="detail-meta-item">发布人：{{ detailData.publisherName || '-' }}</span>
            <span class="detail-meta-item">创建时间：{{ formatTime(detailData.createTime) }}</span>
          </div>
        </div>
      </template>
      <el-divider style="margin: 0 0 16px 0;" />
      <div class="detail-content">{{ detailData.content }}</div>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="640px" :close-on-click-modal="false" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
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
import { useUserStore } from '@/store/user'
import { getAnnouncementListApi, addAnnouncementApi, updateAnnouncementApi, deleteAnnouncementApi } from '@/api/announcement'

const userStore = useUserStore()
const canManage = computed(() => ['ADMIN', 'HR'].includes(userStore.role))

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', status: null, page: 1, size: 10 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getAnnouncementListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
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

// 详情
const detailVisible = ref(false)
const detailData = ref({})
function viewDetail(row) {
  detailData.value = row
  detailVisible.value = true
}

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = ref({})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function openDialog(row) {
  isEdit.value = !!row
  form.value = row ? { ...row } : { title: '', content: '', status: 1 }
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
      await updateAnnouncementApi(form.value)
      ElMessage.success('更新成功')
    } else {
      await addAnnouncementApi(form.value)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

async function handleDelete(row) {
  await deleteAnnouncementApi(row.id)
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
.detail-header {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.detail-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  color: #909399;
}
.detail-content {
  white-space: pre-wrap;
  line-height: 1.8;
  color: #606266;
  font-size: 14px;
}
</style>
