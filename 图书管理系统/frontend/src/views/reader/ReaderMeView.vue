<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">账户设置</div>
    </template>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="读者证号">{{ rAuth.cardNo }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ rAuth.role }}</el-descriptions-item>
      <el-descriptions-item label="读者ID">{{ rAuth.readerId }}</el-descriptions-item>
      <el-descriptions-item label="状态">已登录</el-descriptions-item>
    </el-descriptions>

    <div class="sep"></div>

    <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="form">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" show-password />
      </el-form-item>
      <el-button type="success" :loading="loading" @click="onSubmit">修改密码</el-button>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useReaderAuthStore } from '@/stores/readerAuth'
import { readerChangePasswordApi } from '@/api/readerAuth'

const rAuth = useReaderAuthStore()
const loading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({ oldPassword: '', newPassword: '' })
const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await readerChangePasswordApi(form.oldPassword, form.newPassword)
    ElMessage.success('密码修改成功')
    form.oldPassword = ''
    form.newPassword = ''
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.hdr {
  font-weight: 900;
  color: #0f172a;
}
.sep {
  height: 1px;
  background: rgba(15, 23, 42, 0.06);
  margin: 16px 0;
}
.form {
  max-width: 520px;
}
</style>

