import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

export type ApiResponse<T> = { code: number; message: string; data: T }

export const http = axios.create({
  baseURL: '',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (res) => {
    const body = res.data as ApiResponse<unknown>
    if (body && typeof body.code === 'number' && body.code !== 0) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return res
  },
  (err: AxiosError) => {
    const auth = useAuthStore()
    const status = err.response?.status
    if (status === 401) auth.logout()
    ElMessage.error('网络异常或服务器错误')
    return Promise.reject(err)
  },
)

export async function apiGet<T>(url: string, params?: any) {
  const res = await http.get<ApiResponse<T>>(url, { params })
  return res.data.data
}

export async function apiPost<T>(url: string, data?: any) {
  const res = await http.post<ApiResponse<T>>(url, data)
  return res.data.data
}

export async function apiPut<T>(url: string, data?: any) {
  const res = await http.put<ApiResponse<T>>(url, data)
  return res.data.data
}

export async function apiDelete<T>(url: string) {
  const res = await http.delete<ApiResponse<T>>(url)
  return res.data.data
}

