<template>
  <div class="wrap">
    <div class="panel app-card">
      <div class="header">
        <div class="logo">R</div>
        <div>
          <div class="t1">读者注册</div>
          <div class="t2">创建读者账号后可直接登录使用</div>
        </div>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="onSubmit">
        <el-form-item label="读者证号" prop="cardNo">
          <el-input v-model="form.cardNo" placeholder="自定义证号（例如：R00001）" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-button type="success" size="large" class="btn" :loading="loading" @click="onSubmit">注册并登录</el-button>
      </el-form>

      <div class="link">
        <el-button text @click="$router.push('/reader/login')">已有账号？去登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useReaderAuthStore } from '@/stores/readerAuth'
import { readerRegisterApi } from '@/api/readerAuth'

const router = useRouter()
const rAuth = useReaderAuthStore()
const loading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({ cardNo: '', name: '', phone: '', email: '', password: '' })
const rules: FormRules = {
  cardNo: [{ required: true, message: '请输入读者证号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function onSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await readerRegisterApi(form)
    rAuth.token = res.accessToken
    localStorage.setItem('library_ms_reader_token', res.accessToken)
    rAuth.me = { id: res.readerId, username: res.cardNo, role: res.role }
    localStorage.setItem('library_ms_reader_me', JSON.stringify(rAuth.me))
    rAuth.meLoaded = true
    ElMessage.success('注册成功')
    router.replace('/reader/home')
  } finally {
    loading.value = false
  }
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
  width: 480px;
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
.link {
  display: flex;
  justify-content: center;
  margin-top: 4px;
}
</style>

