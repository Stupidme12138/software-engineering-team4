<template>
  <div class="wrap">
    <div class="panel app-card">
      <div class="header">
        <div class="logo">LM</div>
        <div>
          <div class="t1">图书管理系统</div>
          <div class="t2">Vue3 + Element Plus</div>
        </div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="Admin@123456" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" size="large" class="btn" :loading="loading" @click="onSubmit">登录</el-button>
      </el-form>

      <div class="tips">
        默认管理员：admin / Admin@123456
      </div>
      <div class="link">
        <el-button text @click="goReader">我是读者？去读者端登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({ username: 'admin', password: 'Admin@123456' })

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string | undefined) ?? '/admin/dashboard'
    router.replace(redirect)
  } finally {
    loading.value = false
  }
}

function goReader() {
  router.push('/reader/login')
}
</script>

<style scoped lang="scss">
.wrap {
  height: 100%;
  display: grid;
  place-items: center;
  padding: 16px;
  background:
    radial-gradient(circle at 20% 20%, rgba(96, 165, 250, 0.28), transparent 50%),
    radial-gradient(circle at 80% 30%, rgba(167, 243, 208, 0.26), transparent 55%),
    linear-gradient(180deg, #f6f8fc, #eef2ff);
}
.panel {
  width: 420px;
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(15, 23, 42, 0.06);
  padding: 22px 22px 16px;
  box-shadow: 0 18px 60px rgba(15, 23, 42, 0.12);
}
.header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 12px;
}
.logo {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-weight: 900;
  background: radial-gradient(circle at 30% 30%, #a7f3d0, #60a5fa);
  color: #0b1220;
}
.t1 {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}
.t2 {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}
.btn {
  width: 100%;
  margin-top: 6px;
}
.tips {
  margin-top: 12px;
  color: #64748b;
  font-size: 12px;
  text-align: center;
}
.link {
  display: flex;
  justify-content: center;
  margin-top: 4px;
}
</style>

