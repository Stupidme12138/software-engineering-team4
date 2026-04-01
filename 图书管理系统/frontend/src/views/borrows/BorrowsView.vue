<template>
  <div class="grid">
    <el-card class="app-card" shadow="never">
      <template #header>
        <div class="hdr">
          <div class="title">借书</div>
          <el-tag type="info">需要馆员/管理员权限</el-tag>
        </div>
      </template>

      <el-form :model="borrowForm" label-width="90px">
        <el-form-item label="读者ID" required>
          <el-input-number v-model="borrowForm.readerId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="图书ID" required>
          <el-input-number v-model="borrowForm.bookId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="应还时间">
          <el-date-picker v-model="borrowForm.dueTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="borrowForm.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-button type="primary" :disabled="!canOperate" :loading="borrowLoading" @click="onBorrow">确认借出</el-button>
      </el-form>
    </el-card>

    <el-card class="app-card" shadow="never">
      <template #header>
        <div class="hdr">
          <div class="title">还书</div>
          <el-tag type="info">按借阅记录ID归还</el-tag>
        </div>
      </template>

      <el-form :model="returnForm" label-width="90px">
        <el-form-item label="记录ID" required>
          <el-input-number v-model="returnForm.recordId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-button type="success" :disabled="!canOperate" :loading="returnLoading" @click="onReturn">确认归还</el-button>
      </el-form>
    </el-card>

    <el-card class="app-card full" shadow="never">
      <template #header>
        <div class="hdr">
          <div class="title">借阅记录（快速查看）</div>
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
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { apiGet, apiPost } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

type BorrowRecord = {
  id: number
  readerId: number
  bookId: number
  status: string
  borrowTime: string
  dueTime?: string
  returnTime?: string
}

const auth = useAuthStore()
const canOperate = computed(() => auth.role === 'ADMIN' || auth.role === 'LIBRARIAN')

const borrowForm = reactive({
  readerId: 1,
  bookId: 1,
  dueTime: '' as string | '',
  remark: '',
})
const returnForm = reactive({ recordId: 1 })
const borrowLoading = ref(false)
const returnLoading = ref(false)

async function onBorrow() {
  if (!borrowForm.readerId || !borrowForm.bookId) return ElMessage.warning('请填写读者ID与图书ID')
  borrowLoading.value = true
  try {
    const id = await apiPost<number>('/api/borrows/borrow', {
      readerId: borrowForm.readerId,
      bookId: borrowForm.bookId,
      dueTime: borrowForm.dueTime || null,
      remark: borrowForm.remark || null,
    })
    ElMessage.success(`借出成功，记录ID：${id}`)
    await load()
  } finally {
    borrowLoading.value = false
  }
}

async function onReturn() {
  if (!returnForm.recordId) return ElMessage.warning('请填写记录ID')
  returnLoading.value = true
  try {
    await apiPost<void>('/api/borrows/return', { recordId: returnForm.recordId })
    ElMessage.success('归还成功')
    await load()
  } finally {
    returnLoading.value = false
  }
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
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}
.full {
  grid-column: 1 / -1;
}
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
@media (max-width: 1100px) {
  .grid {
    grid-template-columns: 1fr;
  }
}
</style>

