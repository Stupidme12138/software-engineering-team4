<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">借阅记录查询</div>
        <div class="actions">
          <el-input-number v-model="q.readerId" :min="1" placeholder="读者ID" style="width: 150px" @change="load" />
          <el-input-number v-model="q.bookId" :min="1" placeholder="图书ID" style="width: 150px" @change="load" />
          <el-select v-model="q.status" placeholder="状态" clearable style="width: 140px" @change="load">
            <el-option label="BORROWED" value="BORROWED" />
            <el-option label="RETURNED" value="RETURNED" />
          </el-select>
          <el-button @click="resetQ">重置</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="记录ID" width="90" />
      <el-table-column prop="readerId" label="读者ID" width="90" />
      <el-table-column prop="bookId" label="图书ID" width="90" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="borrowTime" label="借出时间" min-width="180" />
      <el-table-column prop="dueTime" label="应还时间" min-width="180" />
      <el-table-column prop="returnTime" label="归还时间" min-width="180" />
      <el-table-column prop="operatorUserId" label="操作员ID" width="100" />
      <el-table-column prop="remark" label="备注" min-width="220" />
    </el-table>

    <div class="pager">
      <el-pagination
        layout="total, prev, pager, next, sizes"
        :total="total"
        v-model:current-page="q.page"
        v-model:page-size="q.pageSize"
        @change="load"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { apiGet } from '@/api/http'

type BorrowRecord = {
  id: number
  readerId: number
  bookId: number
  status: string
  borrowTime: string
  dueTime?: string
  returnTime?: string
  operatorUserId?: number
  remark?: string
}

const rows = ref<BorrowRecord[]>([])
const total = ref(0)

const q = reactive({
  readerId: undefined as number | undefined,
  bookId: undefined as number | undefined,
  status: '',
  page: 1,
  pageSize: 10,
})

function resetQ() {
  q.readerId = undefined
  q.bookId = undefined
  q.status = ''
  q.page = 1
  q.pageSize = 10
  load()
}

async function load() {
  const res = await apiGet<{ total: number; items: BorrowRecord[] }>('/api/borrows', q)
  rows.value = res.items
  total.value = res.total
}

onMounted(load)
</script>

<style scoped lang="scss">
.hdr {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.title {
  font-weight: 800;
  color: #0f172a;
}
.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>

