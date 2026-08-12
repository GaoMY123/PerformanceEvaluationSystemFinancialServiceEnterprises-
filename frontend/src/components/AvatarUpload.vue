<template>
  <div class="avatar-upload">
    <el-upload
      :show-file-list="false"
      :before-upload="beforeUpload"
      :http-request="handleUpload"
      accept="image/*"
    >
      <div class="avatar-wrapper">
        <template v-if="modelValue">
          <img :src="modelValue" class="avatar-img" />
        </template>
        <template v-else>
          <div class="avatar-placeholder">
            <el-icon :size="28"><Plus /></el-icon>
            <span>上传头像</span>
          </div>
        </template>
        <div class="avatar-hover-mask">
          <el-icon :size="20"><Camera /></el-icon>
        </div>
      </div>
    </el-upload>
  </div>
</template>

<script setup>
import { Plus, Camera } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadAvatarApi } from '@/api/user'

const props = defineProps({
  modelValue: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue'])

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleUpload({ file }) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await uploadAvatarApi(formData)
    emit('update:modelValue', res.data)
    ElMessage.success('头像上传成功')
  } catch (e) {
    // error handled by interceptor
  }
}
</script>

<style scoped>
.avatar-upload {
  display: inline-block;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 1px dashed #d9d9d9;
  background: #fafafa;
}

.avatar-wrapper:hover .avatar-hover-mask {
  opacity: 1;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  font-size: 12px;
  gap: 4px;
}

.avatar-hover-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}
</style>
