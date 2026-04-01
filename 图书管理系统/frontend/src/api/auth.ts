import { apiGet, apiPost } from './http'

export type LoginResponse = {
  accessToken: string
  userId: number
  username: string
  role: string
}

export function loginApi(username: string, password: string) {
  return apiPost<LoginResponse>('/api/auth/login', { username, password })
}

export type Me = { id: number; username: string; role: string }
export function meApi() {
  return apiGet<Me>('/api/auth/me')
}

export function changePasswordApi(oldPassword: string, newPassword: string) {
  return apiPost<void>('/api/auth/change-password', { oldPassword, newPassword })
}

