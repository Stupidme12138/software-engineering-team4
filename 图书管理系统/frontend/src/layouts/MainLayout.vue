<template>
  <div class="layout">
    <aside class="sider">
      <div class="brand">
        <div class="logo">LM</div>
        <div class="title">
          <div class="name">图书管理</div>
          <div class="sub">Library MS</div>
        </div>
      </div>
      <el-menu :default-active="activePath" router class="menu" background-color="transparent">
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>概览</span>
        </el-menu-item>
        <el-menu-item index="/admin/books">
          <el-icon><Notebook /></el-icon>
          <span>图书信息</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Collection /></el-icon>
          <span>图书分类</span>
        </el-menu-item>
        <el-menu-item index="/admin/readers">
          <el-icon><User /></el-icon>
          <span>读者信息</span>
        </el-menu-item>
        <el-menu-item v-if="auth.role === 'LIBRARIAN'" index="/admin/borrows">
          <el-icon><Reading /></el-icon>
          <span>借阅归还</span>
        </el-menu-item>
        <el-menu-item index="/admin/records">
          <el-icon><Document /></el-icon>
          <span>借阅记录</span>
        </el-menu-item>
        <el-menu-item v-if="auth.isAdmin" index="/admin/users">
          <el-icon><Setting /></el-icon>
          <span>账号管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/me">
          <el-icon><Avatar /></el-icon>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <main class="main">
      <header class="topbar app-card">
        <div class="left">
          <div class="crumb">{{ title }}</div>
        </div>
        <div class="right">
          <el-tag effect="light" type="info">{{ auth.role }}</el-tag>
          <span class="user">{{ auth.username }}</span>
          <el-button text @click="onLogout">退出</el-button>
        </div>
      </header>

      <section class="content">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'
import { Avatar, Collection, DataLine, Document, Notebook, Reading, Setting, User } from '@element-plus/icons-vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const activePath = computed(() => route.path)
const title = computed(() => (route.meta.title as string | undefined) ?? '图书管理系统')

async function onLogout() {
  await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
  auth.logout()
  router.replace('/')
}
</script>

<style scoped lang="scss">
.layout {
  height: 100%;
  display: flex;
  gap: 16px;
  padding: 16px;
  box-sizing: border-box;
}
.sider {
  width: 240px;
  border-radius: 16px;
  background: linear-gradient(180deg, #0b1220, #0a1c2e);
  color: #e6eefc;
  padding: 14px 10px;
  box-sizing: border-box;
  box-shadow: 0 8px 26px rgba(10, 20, 40, 0.18);
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 10px 14px;
}
.logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  font-weight: 800;
  color: #0b1220;
  background: radial-gradient(circle at 30% 30%, #a7f3d0, #60a5fa);
}
.title .name {
  font-weight: 700;
  letter-spacing: 0.5px;
}
.title .sub {
  font-size: 12px;
  opacity: 0.75;
  margin-top: 2px;
}
.menu {
  border-right: none;
  --el-menu-text-color: rgba(230, 238, 252, 0.86);
  --el-menu-active-color: #a7f3d0;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.06);
}
.main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.topbar {
  background: #fff;
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06);
}
.crumb {
  font-weight: 700;
  color: #0f172a;
}
.right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user {
  color: #334155;
  font-weight: 600;
}
.content {
  flex: 1;
  min-height: 0;
}
</style>

