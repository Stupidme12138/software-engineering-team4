<template>
  <div class="grid">
    <el-card class="app-card full" shadow="never">
      <div class="hero">
        <div>
          <div class="t1">你好，{{ rAuth.cardNo }}</div>
          <div class="t2">在这里你可以查询图书，并发起借阅申请（直接借出）。</div>
        </div>
        <el-button type="success" @click="$router.push('/reader/books')">去查书</el-button>
      </div>
    </el-card>

    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">当前借出中</div>
        <div class="value">{{ borrowedCount }}</div>
      </div>
    </el-card>
    <el-card class="app-card" shadow="never">
      <div class="kpi">
        <div class="label">历史借阅记录</div>
        <div class="value">{{ totalRecords }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useReaderAuthStore } from '@/stores/readerAuth'
import { readerGet } from '@/api/httpReader'

const rAuth = useReaderAuthStore()
const borrowedCount = ref('-')
const totalRecords = ref('-')

onMounted(async () => {
  const borrowed = await readerGet<{ total: number; items: any[] }>('/api/reader/borrows', { status: 'BORROWED', page: 1, pageSize: 1 })
  borrowedCount.value = String(borrowed.total)
  const all = await readerGet<{ total: number; items: any[] }>('/api/reader/borrows', { page: 1, pageSize: 1 })
  totalRecords.value = String(all.total)
})
</script>

<style scoped lang="scss">
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}
.full {
  grid-column: 1 / -1;
}
.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 6px 2px;
}
.t1 {
  font-size: 18px;
  font-weight: 900;
  color: #0f172a;
}
.t2 {
  margin-top: 6px;
  color: #64748b;
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
}
@media (max-width: 1100px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>

