<template>
  <div class="wrap">
    <div class="panel app-card">
      <div class="header">
        <div class="logo">R</div>
        <div>
          <div class="t1">读者登录</div>
          <div class="t2">查询图书、发起借阅、查看借阅历史</div>
        </div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="读者证号" prop="cardNo">
          <el-input v-model="form.cardNo" placeholder="例如：R00001" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="success" size="large" class="btn" :loading="loading" @click="onSubmit">登录</el-button>
      </el-form>

      <div class="tips">管理员在后台创建读者时，默认初始密码：Reader@123456（可自行修改）</div>
      <div class="link">
        <el-button text @click="goRegister">没有账号？去注册</el-button>
        <el-button text @click="goAdmin">去管理端登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { useReaderAuthStore } from '@/stores/readerAuth'

const rAuth = useReaderAuthStore()
const route = useRoute()
const router = useRouter()

const loading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({ cardNo: '', password: '' })

const rules: FormRules = {
  cardNo: [{ required: true, message: '请输入读者证号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await rAuth.login(form.cardNo, form.password)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string | undefined) ?? '/reader/home'
    router.replace(redirect)
  } finally {
    loading.value = false
  }
}

function goAdmin() {
  router.push('/login')
}

function goRegister() {
  router.push('/reader/register')
}
</script>

<style scoped lang="scss">
.wrap {
  height: 100%;
  display: grid;
  place-items: center;
  padding: 16px;
  background:
    radial-gradient(circle at 25% 20%, rgba(52, 211, 153, 0.22), transparent 55%),
    radial-gradient(circle at 80% 30%, rgba(96, 165, 250, 0.18), transparent 55%),
    linear-gradient(180deg, #f6f8fc, #ecfdf5);
}
.panel {
  width: 440px;
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
  background: radial-gradient(circle at 30% 30%, #a7f3d0, #34d399);
  color: #0b1220;
}
.t1 {
  font-size: 18px;
  font-weight: 900;
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

