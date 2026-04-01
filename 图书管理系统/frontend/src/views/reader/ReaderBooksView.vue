<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">图书查询</div>
        <div class="actions">
          <el-input v-model="q.keyword" placeholder="书名/作者/ISBN/出版社/书架位置" clearable style="width: 320px" @change="load" />
          <el-button type="success" @click="load">搜索</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="图书ID" width="90" />
      <el-table-column prop="title" label="书名" min-width="220" />
      <el-table-column prop="author" label="作者" width="140" />
      <el-table-column prop="publisher" label="出版社" width="160" />
      <el-table-column prop="availableQty" label="可借" width="90" />
      <el-table-column prop="shelfLocation" label="位置" width="120" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="success" plain :disabled="row.availableQty <= 0 || row.status !== 'ENABLED'" :loading="borrowLoadingId === row.id" @click="borrow(row)">
            借阅
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
import { ElMessage } from 'element-plus'
import { readerGet, readerPost } from '@/api/httpReader'

type Book = {
  id: number
  title: string
  author?: string
  publisher?: string
  shelfLocation?: string
  availableQty: number
  status: string
}

const rows = ref<Book[]>([])
const total = ref(0)
const q = reactive({ keyword: '', page: 1, pageSize: 10 })
const borrowLoadingId = ref<number | null>(null)

async function load() {
  // 读者端必须用 readerGet（携带读者 JWT），否则会被后端拦截为 403/401
  const res = await readerGet<{ total: number; items: Book[] }>('/api/books', q)
  rows.value = res.items
  total.value = res.total
}

async function borrow(row: Book) {
  borrowLoadingId.value = row.id
  try {
    const recordId = await readerPost<number>('/api/reader/borrow', { readerId: 0, bookId: row.id })
    ElMessage.success(`借阅成功，记录ID：${recordId}`)
    await load()
  } finally {
    borrowLoadingId.value = null
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

