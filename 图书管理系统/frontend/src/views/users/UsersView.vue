<template>
  <el-card class="app-card" shadow="never">
    <template #header>
      <div class="hdr">
        <div class="title">账号管理（ADMIN）</div>
        <div class="actions">
          <el-input v-model="q.keyword" placeholder="用户名/角色" clearable style="width: 220px" @change="load" />
          <el-button type="primary" @click="openCreate">新增账号</el-button>
        </div>
      </div>
    </template>

    <el-table :data="rows" border>
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="username" label="用户名" width="200" />
      <el-table-column prop="role" label="角色" width="140" />
      <el-table-column prop="enabled" label="启用" width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'danger'">{{ row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      <el-table-column prop="updatedAt" label="更新时间" min-width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button text type="warning" @click="openReset(row)">重置密码</el-button>
          <el-button text type="danger" @click="onDelete(row)">删除</el-button>
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

  <el-dialog v-model="dlg.open" :title="dlg.mode === 'create' ? '新增账号' : '编辑账号'" width="560px">
    <el-form :model="dlg.form" label-width="90px">
      <el-form-item label="用户名" required>
        <el-input v-model="dlg.form.username" />
      </el-form-item>
      <el-form-item v-if="dlg.mode === 'create'" label="密码" required>
        <el-input v-model="dlg.form.password" show-password />
      </el-form-item>
      <el-form-item label="角色" required>
        <el-select v-model="dlg.form.role" style="width: 100%">
          <el-option label="ADMIN" value="ADMIN" />
          <el-option label="LIBRARIAN" value="LIBRARIAN" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用">
        <el-switch v-model="dlg.form.enabled" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dlg.open = false">取消</el-button>
      <el-button type="primary" :loading="dlg.loading" @click="onSave">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="reset.open" title="重置密码" width="520px">
    <el-form :model="reset.form" label-width="90px">
      <el-form-item label="用户名">
        <el-input :model-value="reset.username" disabled />
      </el-form-item>
      <el-form-item label="新密码" required>
        <el-input v-model="reset.form.newPassword" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reset.open = false">取消</el-button>
      <el-button type="primary" :loading="reset.loading" @click="onResetSave">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { apiDelete, apiGet, apiPost, apiPut } from '@/api/http'

type UserDto = {
  id: number
  username: string
  role: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

const rows = ref<UserDto[]>([])
const total = ref(0)
const q = reactive({ keyword: '', page: 1, pageSize: 10 })

async function load() {
  const res = await apiGet<{ total: number; items: UserDto[] }>('/api/users', q)
  rows.value = res.items
  total.value = res.total
}

const dlg = reactive({
  open: false,
  mode: 'create' as 'create' | 'edit',
  loading: false,
  id: null as number | null,
  form: { username: '', password: '', role: 'LIBRARIAN', enabled: true },
})

function openCreate() {
  dlg.mode = 'create'
  dlg.id = null
  Object.assign(dlg.form, { username: '', password: '', role: 'LIBRARIAN', enabled: true })
  dlg.open = true
}

function openEdit(row: UserDto) {
  dlg.mode = 'edit'
  dlg.id = row.id
  Object.assign(dlg.form, { username: row.username, password: '', role: row.role, enabled: row.enabled })
  dlg.open = true
}

async function onSave() {
  if (!dlg.form.username.trim()) return ElMessage.warning('请填写用户名')
  if (dlg.mode === 'create' && !dlg.form.password.trim()) return ElMessage.warning('请填写密码')
  dlg.loading = true
  try {
    if (dlg.mode === 'create') {
      await apiPost<number>('/api/users', dlg.form)
      ElMessage.success('新增成功')
    } else {
      await apiPut<void>(`/api/users/${dlg.id}`, {
        username: dlg.form.username,
        role: dlg.form.role,
        enabled: dlg.form.enabled,
      })
      ElMessage.success('保存成功')
    }
    dlg.open = false
    await load()
  } finally {
    dlg.loading = false
  }
}

const reset = reactive({
  open: false,
  loading: false,
  id: 0,
  username: '',
  form: { newPassword: '' },
})

function openReset(row: UserDto) {
  reset.id = row.id
  reset.username = row.username
  reset.form.newPassword = ''
  reset.open = true
}

async function onResetSave() {
  if (!reset.form.newPassword.trim()) return ElMessage.warning('请填写新密码')
  reset.loading = true
  try {
    await apiPost<void>(`/api/users/${reset.id}/reset-password`, reset.form)
    ElMessage.success('重置成功')
    reset.open = false
  } finally {
    reset.loading = false
  }
}

async function onDelete(row: UserDto) {
  await ElMessageBox.confirm(`确认删除账号「${row.username}」吗？`, '提示', { type: 'warning' })
  await apiDelete<void>(`/api/users/${row.id}`)
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

