<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">读者信息</div>
        <div class="actions">
          <el-input v-model="q.keyword" placeholder="证号/姓名/手机" clearable style="width: 240px" @change="load" />
          <el-select v-model="q.status" placeholder="状态" clearable style="width: 140px" @change="load">
            <el-option label="ACTIVE" value="ACTIVE" />
            <el-option label="CANCELLED" value="CANCELLED" />
          </el-select>
          <el-button type="primary" :disabled="!canEdit" @click="openCreate">新增读者</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="cardNo" label="证号" width="140" />
      <el-table-column prop="name" label="姓名" width="140" />
      <el-table-column prop="phone" label="手机" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="openHistory(row)">借阅历史</el-button>
          <el-button text type="primary" :disabled="!canEdit" @click="openEdit(row)">编辑</el-button>
          <el-button text type="warning" :disabled="!canEdit || row.status === 'CANCELLED'" @click="onCancel(row)">
            注销
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

  <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增读者' : '编辑读者'" width="640px">
    <el-form :model="dlg.form" label-width="90px">
      <el-form-item label="证号" required>
        <el-input v-model="dlg.form.cardNo" />
      </el-form-item>
      <el-form-item label="姓名" required>
        <el-input v-model="dlg.form.name" />
      </el-form-item>
      <el-form-item label="手机">
        <el-input v-model="dlg.form.phone" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="dlg.form.email" />
      </el-form-item>
      <el-form-item label="身份证">
        <el-input v-model="dlg.form.idCard" />
      </el-form-item>
      <el-form-item v-if="dlg.mode === 'edit'" label="状态">
        <el-select v-model="dlg.form.status" style="width: 100%">
          <el-option label="ACTIVE" value="ACTIVE" />
          <el-option label="CANCELLED" value="CANCELLED" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg.open = false">取消</el-button>
      <el-button type="primary" :loading="dlg.loading" @click="onSave">保存</el-button>
    </template>
  </el-dialog>

  <el-drawer v-model="hist.open" :title="`借阅历史 - ${hist.name}`" size="55%">
    <div class="hist-head">
      <el-tag type="info">读者ID：{{ hist.readerId }}</el-tag>
    </div>
    <el-table :data="hist.rows" border>
      <el-table-column prop="id" label="记录ID" width="90" />
      <el-table-column prop="bookId" label="图书ID" width="90" />
      <el-table-column prop="status" label="状态" width="120" />
      <el-table-column prop="borrowTime" label="借出时间" min-width="180" />
      <el-table-column prop="dueTime" label="应还时间" min-width="180" />
      <el-table-column prop="returnTime" label="归还时间" min-width="180" />
    </el-table>
    <div class="pager">
      <el-pagination
        layout="total, prev, pager, next, sizes"
        :total="hist.total"
        v-model:current-page="hist.page"
        v-model:page-size="hist.pageSize"
        @change="loadHistory"
      />
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiGet, apiPost, apiPut } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

type Reader = {
  id: number
  cardNo: string
  name: string
  phone?: string
  email?: string
  idCard?: string
  status: string
}

type BorrowRecord = {
  id: number
  readerId: number
  bookId: number
  borrowTime: string
  dueTime?: string
  returnTime?: string
  status: string
}

const auth = useAuthStore()
const canEdit = computed(() => !!auth.role)

const rows = ref<Reader[]>([])
const total = ref(0)
const q = reactive({ keyword: '', status: '', page: 1, pageSize: 10 })

async function load() {
  const res = await apiGet<{ total: number; items: Reader[] }>('/api/readers', q)
  rows.value = res.items
  total.value = res.total
}

const dlg = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  loading: false,
  id: null as number | null,
  form: { cardNo: '', name: '', phone: '', email: '', idCard: '', status: 'ACTIVE' },
})

function openCreate() {
  dlg.mode = 'create'
  dlg.id = null
  Object.assign(dlg.form, { cardNo: '', name: '', phone: '', email: '', idCard: '', status: 'ACTIVE' })
  dlg.open = true
}

function openEdit(row: Reader) {
  dlg.mode = 'edit'
  dlg.id = row.id
  Object.assign(dlg.form, {
    cardNo: row.cardNo ?? '',
    name: row.name ?? '',
    phone: row.phone ?? '',
    email: row.email ?? '',
    idCard: row.idCard ?? '',
    status: row.status ?? 'ACTIVE',
  })
  dlg.open = true
}

async function onSave() {
  if (!dlg.form.cardNo.trim()) return ElMessage.warning('请填写读者证号')
  if (!dlg.form.name.trim()) return ElMessage.warning('请填写姓名')
  dlg.loading = true
  try {
    if (dlg.mode === 'create') {
      await apiPost<number>('/api/readers', {
        cardNo: dlg.form.cardNo,
        name: dlg.form.name,
        phone: dlg.form.phone,
        email: dlg.form.email,
        idCard: dlg.form.idCard,
      })
      ElMessage.success('新增成功')
    } else {
      await apiPut<void>(`/api/readers/${dlg.id}`, dlg.form)
      ElMessage.success('保存成功')
    }
    dlg.open = false
    await load()
  } finally {
    dlg.loading = false
  }
}

async function onCancel(row: Reader) {
  await ElMessageBox.confirm(`确认注销读者「${row.name}」吗？`, '提示', { type: 'warning' })
  await apiPost<void>(`/api/readers/${row.id}/cancel`)
  ElMessage.success('已注销')
  await load()
}

const hist = reactive({
  open: false,
  readerId: 0,
  name: '',
  rows: [] as BorrowRecord[],
  total: 0,
  page: 1,
  pageSize: 10,
})

async function loadHistory() {
  const res = await apiGet<{ total: number; items: BorrowRecord[] }>(`/api/borrows/reader/${hist.readerId}`, {
    page: hist.page,
    pageSize: hist.pageSize,
  })
  hist.rows = res.items
  hist.total = res.total
}

async function openHistory(row: Reader) {
  hist.readerId = row.id
  hist.name = row.name
  hist.page = 1
  hist.pageSize = 10
  hist.open = true
  await loadHistory()
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
.hist-head {
  margin-bottom: 10px;
}
</style>

