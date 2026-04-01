<template>
  <div class="wrap">
    <div class="panel app-card">
      <div class="header">
        <div class="logo">LM</div>
        <div>
          <div class="t1">图书管理系统</div>
          <div class="t2">请选择进入管理端或读者端</div>
        </div>
      </div>

      <div class="grid">
        <el-card class="card" shadow="never" @click="goAdmin">
          <div class="c1">管理端</div>
          <div class="c2">管理员 / 馆员登录</div>
        </el-card>
        <el-card class="card" shadow="never" @click="goReader">
          <div class="c1">读者端</div>
          <div class="c2">读者登录 / 注册</div>
        </el-card>
      </div>

      <div class="tips">建议：读者端用于借/还书与个人借阅记录；管理端用于图书、分类、读者信息与借阅记录管理。</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
function goAdmin() {
  router.push('/login')
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
    radial-gradient(circle at 20% 20%, rgba(96, 165, 250, 0.22), transparent 55%),
    radial-gradient(circle at 80% 30%, rgba(167, 243, 208, 0.22), transparent 55%),
    linear-gradient(180deg, #f6f8fc, #eef2ff);
}
.panel {
  width: 620px;
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
  margin-bottom: 14px;
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
  font-weight: 900;
  color: #0f172a;
}
.t2 {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
.card {
  cursor: pointer;
  border-radius: 14px;
  transition: transform 0.08s ease, box-shadow 0.08s ease;
}
.card:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
}
.c1 {
  font-weight: 900;
  color: #0f172a;
  font-size: 16px;
}
.c2 {
  margin-top: 6px;
  color: #64748b;
}
.tips {
  margin-top: 12px;
  color: #64748b;
  font-size: 12px;
  text-align: center;
}
@media (max-width: 720px) {
  .panel {
    width: 100%;
  }
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>

