<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">图书信息</div>
        <div class="actions">
          <el-input v-model="q.keyword" placeholder="书名/作者/ISBN/出版社/书架位置" clearable style="width: 320px" @change="load" />
          <el-select v-model="q.categoryId" placeholder="分类" clearable style="width: 160px" @change="load">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
          <el-button type="primary" :disabled="!canEdit" @click="openCreate">新增图书</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="书名" min-width="200" />
      <el-table-column prop="author" label="作者" width="140" />
      <el-table-column prop="isbn" label="ISBN" width="160" />
      <el-table-column prop="publisher" label="出版社" width="160" />
      <el-table-column prop="shelfLocation" label="书架位置" width="120" />
      <el-table-column prop="totalQty" label="馆藏" width="90" />
      <el-table-column prop="availableQty" label="可借" width="90" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" :disabled="!canEdit" @click="openEdit(row)">编辑</el-button>
          <el-button text type="danger" :disabled="!isAdmin" @click="onDelete(row)">删除</el-button>
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

  <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增图书' : '编辑图书'" width="720px">
    <el-form :model="dlg.form" label-width="100px">
      <el-form-item label="书名" required>
        <el-input v-model="dlg.form.title" />
      </el-form-item>
      <el-form-item label="作者">
        <el-input v-model="dlg.form.author" />
      </el-form-item>
      <el-form-item label="ISBN">
        <el-input v-model="dlg.form.isbn" />
      </el-form-item>
      <el-form-item label="出版社">
        <el-input v-model="dlg.form.publisher" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="dlg.form.categoryId" clearable style="width: 100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="学科">
        <el-input v-model="dlg.form.subject" />
      </el-form-item>
      <el-form-item label="书架位置">
        <el-input v-model="dlg.form.shelfLocation" />
      </el-form-item>
      <el-form-item label="馆藏数量" required>
        <el-input-number v-model="dlg.form.totalQty" :min="1" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="dlg.form.status" style="width: 100%">
          <el-option label="ENABLED" value="ENABLED" />
          <el-option label="DISABLED" value="DISABLED" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg.open = false">取消</el-button>
      <el-button type="primary" :loading="dlg.loading" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiDelete, apiGet, apiPost, apiPut } from '@/api/http'
import { useAuthStore } from '@/stores/auth'

type Category = { id: number; name: string }
type Book = {
  id: number
  isbn?: string
  title: string
  author?: string
  publisher?: string
  categoryId?: number
  subject?: string
  shelfLocation?: string
  totalQty: number
  availableQty: number
  status: string
}

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)
const canEdit = computed(() => !!auth.role)

const categories = ref<Category[]>([])
const rows = ref<Book[]>([])
const total = ref(0)

const q = reactive({ keyword: '', categoryId: undefined as number | undefined, page: 1, pageSize: 10 })

async function load() {
  const res = await apiGet<{ total: number; items: Book[] }>('/api/books', q)
  rows.value = res.items
  total.value = res.total
}

async function loadCategories() {
  categories.value = await apiGet<Category[]>('/api/categories/all')
}

const dlg = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  loading: false,
  id: null as number | null,
  form: {
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    categoryId: undefined as number | undefined,
    subject: '',
    shelfLocation: '',
    totalQty: 1,
    status: 'ENABLED',
  },
})

function openCreate() {
  dlg.mode = 'create'
  dlg.id = null
  Object.assign(dlg.form, {
    title: '',
    author: '',
    isbn: '',
    publisher: '',
    categoryId: undefined,
    subject: '',
    shelfLocation: '',
    totalQty: 1,
    status: 'ENABLED',
  })
  dlg.open = true
}

function openEdit(row: Book) {
  dlg.mode = 'edit'
  dlg.id = row.id
  Object.assign(dlg.form, {
    title: row.title ?? '',
    author: row.author ?? '',
    isbn: row.isbn ?? '',
    publisher: row.publisher ?? '',
    categoryId: row.categoryId,
    subject: row.subject ?? '',
    shelfLocation: row.shelfLocation ?? '',
    totalQty: row.totalQty ?? 1,
    status: row.status ?? 'ENABLED',
  })
  dlg.open = true
}

async function onSave() {
  if (!dlg.form.title.trim()) return ElMessage.warning('请填写书名')
  dlg.loading = true
  try {
    const payload = {
      ...dlg.form,
      // 避免空字符串触发数据库 UNIQUE 冲突（isbn 多条空字符串会冲突；NULL 不会）
      isbn: dlg.form.isbn.trim() ? dlg.form.isbn.trim() : null,
    }
    if (dlg.mode === 'create') {
      await apiPost<number>('/api/books', payload)
      ElMessage.success('新增成功')
    } else {
      await apiPut<void>(`/api/books/${dlg.id}`, payload)
      ElMessage.success('保存成功')
    }
    dlg.open = false
    await load()
  } finally {
    dlg.loading = false
  }
}

async function onDelete(row: Book) {
  await ElMessageBox.confirm(`确认删除《${row.title}》吗？`, '提示', { type: 'warning' })
  await apiDelete<void>(`/api/books/${row.id}`)
  ElMessage.success('删除成功')
  await load()
}

onMounted(async () => {
  await loadCategories()
  await load()
})
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

