<template>
  <div class="page-container">
    <el-card shadow="never">
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="query.username" placeholder="用户名" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="query.keyword" placeholder="操作描述" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="query.result" placeholder="全部" clearable style="width: 100px;">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%" :scrollbar-always-on="true">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="username" label="操作人" width="120" show-overflow-tooltip />
        <el-table-column prop="operation" label="操作描述" width="160" show-overflow-tooltip />
        <el-table-column prop="method" label="请求方法" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="130" show-overflow-tooltip />
        <el-table-column prop="result" label="结果" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.result === 1 ? 'success' : 'danger'" size="small" effect="light">
              {{ row.result === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="170" show-overflow-tooltip>
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="640px" :close-on-click-modal="false">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ detailData.operation }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作结果">
          <el-tag :type="detailData.result === 1 ? 'success' : 'danger'" size="small" effect="light">
            {{ detailData.result === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ detailData.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <div class="log-json">{{ formatJson(detailData.params) }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2" v-if="detailData.errorMsg">
          <div class="log-error">{{ detailData.errorMsg }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLogListApi } from '@/api/log'

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

function formatJson(str) {
  if (!str) return '-'
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch (e) {
    return str
  }
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ keyword: '', username: '', result: null, page: 1, size: 10 })

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getLogListApi(query.value)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function handleSearch() {
  query.value.page = 1
  loadData()
}

function handleReset() {
  query.value = { keyword: '', username: '', result: null, page: 1, size: 10 }
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
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}
.mt-16 {
  margin-top: 16px;
  justify-content: flex-end;
}
.log-json {
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
  font-size: 12px;
  color: #606266;
  background: #f5f7fa;
  padding: 8px;
  border-radius: 4px;
}
.log-error {
  white-space: pre-wrap;
  word-break: break-all;
  color: #f56c6c;
  font-size: 13px;
}
</style>
