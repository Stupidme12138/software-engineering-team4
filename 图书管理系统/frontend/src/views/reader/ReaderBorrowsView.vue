<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">我的借阅</div>
        <div class="actions">
          <el-select v-model="q.status" placeholder="状态" clearable style="width: 160px" @change="load">
            <el-option label="BORROWED" value="BORROWED" />
            <el-option label="RETURNED" value="RETURNED" />
          </el-select>
          <el-button @click="resetQ">重置</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="记录ID" width="90" />
      <el-table-column prop="bookId" label="图书ID" width="90" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="borrowTime" label="借出时间" min-width="180" />
      <el-table-column prop="dueTime" label="应还时间" min-width="180" />
      <el-table-column prop="returnTime" label="归还时间" min-width="180" />
      <el-table-column prop="remark" label="备注" min-width="220" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'BORROWED'"
            type="success"
            plain
            :loading="returningId === row.id"
            @click="returnBook(row)"
          >
            归还
          </el-button>
        </template>
      </el-table-column>
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
import { readerGet, readerPost } from '@/api/httpReader'
import { ElMessage, ElMessageBox } from 'element-plus'

type BorrowRecord = {
  id: number
  bookId: number
  status: string
  borrowTime: string
  dueTime?: string
  returnTime?: string
  remark?: string
}

const rows = ref<BorrowRecord[]>([])
const total = ref(0)
const q = reactive({ status: '', page: 1, pageSize: 10 })
const returningId = ref<number | null>(null)

function resetQ() {
  q.status = ''
  q.page = 1
  q.pageSize = 10
  load()
}

async function load() {
  const res = await readerGet<{ total: number; items: BorrowRecord[] }>('/api/reader/borrows', q)
  rows.value = res.items
  total.value = res.total
}

async function returnBook(row: BorrowRecord) {
  await ElMessageBox.confirm(`确认归还该记录（ID: ${row.id}）吗？`, '提示', { type: 'warning' })
  returningId.value = row.id
  try {
    await readerPost<void>('/api/reader/return', { recordId: row.id })
    ElMessage.success('归还成功')
    await load()
  } finally {
    returningId.value = null
  }
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
  font-weight: 900;
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

