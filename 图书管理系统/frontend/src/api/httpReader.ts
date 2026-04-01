import axios, { type AxiosError } from 'axios'
import { ElMessage } from 'element-plus'
import { useReaderAuthStore } from '@/stores/readerAuth'

export type ApiResponse<T> = { code: number; message: string; data: T }

export const httpReader = axios.create({
  baseURL: '',
  timeout: 15000,
})

httpReader.interceptors.request.use((config) => {
  const auth = useReaderAuthStore()
  if (auth.token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

httpReader.interceptors.response.use(
  (res) => {
    const body = res.data as ApiResponse<unknown>
    if (body && typeof body.code === 'number' && body.code !== 0) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || 'Request failed'))
    }
    return res
  },
  (err: AxiosError) => {
    const auth = useReaderAuthStore()
    const status = err.response?.status
    if (status === 401) auth.logout()
    ElMessage.error('网络异常或服务器错误')
    return Promise.reject(err)
  },
)

export async function readerGet<T>(url: string, params?: any) {
  const res = await httpReader.get<ApiResponse<T>>(url, { params })
  return res.data.data
}

export async function readerPost<T>(url: string, data?: any) {
  const res = await httpReader.post<ApiResponse<T>>(url, data)
  return res.data.data
}

