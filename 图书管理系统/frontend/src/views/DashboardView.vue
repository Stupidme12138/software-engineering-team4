<template>
  <div class="grid">
    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">图书总数</div>
        <div class="value">{{ stats?.totalBooks ?? '-' }}</div>
      </div>
    </el-card>
    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">可借库存</div>
        <div class="value">{{ stats?.availableBooks ?? '-' }}</div>
      </div>
    </el-card>
    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">活跃读者</div>
        <div class="value">{{ stats?.activeReaders ?? '-' }}</div>
      </div>
    </el-card>
    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">借出中</div>
        <div class="value">{{ stats?.borrowedCount ?? '-' }}</div>
      </div>
    </el-card>

    <el-card class="app-card full" shadow="never">
      <div class="welcome">
        <div class="t1">欢迎回来，{{ auth.username }}</div>
        <div class="t2">你可以从左侧菜单开始进行图书、分类、读者、借阅归还等管理。</div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { apiGet } from '@/api/http'

type OverviewStats = {
  totalBooks: number
  enabledBooks: number
  availableBooks: number
  activeReaders: number
  borrowedCount: number
  totalBorrowRecords: number
}

const auth = useAuthStore()
const stats = ref<OverviewStats | null>(null)

onMounted(async () => {
  stats.value = await apiGet<OverviewStats>('/api/stats/overview')
})
</script>

<style scoped lang="scss">
.grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.full {
  grid-column: 1 / -1;
}
.kpi .label {
  color: #64748b;
  font-size: 12px;
}
.kpi .value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 900;
  color: #0f172a;
  letter-spacing: 0.4px;
}
.welcome {
  padding: 8px 4px;
}
.welcome .t1 {
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
}
.welcome .t2 {
  margin-top: 6px;
  color: #64748b;
}
@media (max-width: 1100px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>

