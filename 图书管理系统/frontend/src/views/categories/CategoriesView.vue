<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">图书分类</div>
        <div class="actions">
          <el-input v-model="q.keyword" placeholder="分类名称/学科" clearable style="width: 260px" @change="load" />
          <el-input v-model="q.subject" placeholder="学科筛选" clearable style="width: 160px" @change="load" />
          <el-button type="primary" :disabled="!canEdit" @click="openCreate">新增分类</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="name" label="分类名称" min-width="200" />
      <el-table-column prop="subject" label="学科" width="160" />
      <el-table-column prop="description" label="描述" min-width="260" />
      <el-table-column label="操作" width="200" fixed="right">
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

  <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增分类' : '编辑分类'" width="620px">
    <el-form :model="dlg.form" label-width="90px">
      <el-form-item label="名称" required>
        <el-input v-model="dlg.form.name" />
      </el-form-item>
      <el-form-item label="学科">
        <el-input v-model="dlg.form.subject" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="dlg.form.description" type="textarea" :rows="3" />
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

type Category = { id: number; name: string; subject?: string; description?: string }

const auth = useAuthStore()
const isAdmin = computed(() => auth.isAdmin)
const canEdit = computed(() => !!auth.role)

const rows = ref<Category[]>([])
const total = ref(0)
const q = reactive({ keyword: '', subject: '', page: 1, pageSize: 10 })

async function load() {
  const res = await apiGet<{ total: number; items: Category[] }>('/api/categories', q)
  rows.value = res.items
  total.value = res.total
}

const dlg = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  loading: false,
  id: null as number | null,
  form: { name: '', subject: '', description: '' },
})

function openCreate() {
  dlg.mode = 'create'
  dlg.id = null
  Object.assign(dlg.form, { name: '', subject: '', description: '' })
  dlg.open = true
}

function openEdit(row: Category) {
  dlg.mode = 'edit'
  dlg.id = row.id
  Object.assign(dlg.form, { name: row.name ?? '', subject: row.subject ?? '', description: row.description ?? '' })
  dlg.open = true
}

async function onSave() {
  if (!dlg.form.name.trim()) return ElMessage.warning('请填写分类名称')
  dlg.loading = true
  try {
    if (dlg.mode === 'create') {
      await apiPost<number>('/api/categories', dlg.form)
      ElMessage.success('新增成功')
    } else {
      await apiPut<void>(`/api/categories/${dlg.id}`, dlg.form)
      ElMessage.success('保存成功')
    }
    dlg.open = false
    await load()
  } finally {
    dlg.loading = false
  }
}

async function onDelete(row: Category) {
  await ElMessageBox.confirm(`确认删除分类「${row.name}」吗？`, '提示', { type: 'warning' })
  await apiDelete<void>(`/api/categories/${row.id}`)
  ElMessage.success('删除成功')
  await load()
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

