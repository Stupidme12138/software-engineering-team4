import { readerGet, readerPost } from './httpReader'

export type ReaderLoginResponse = {
  accessToken: string
  readerId: number
  cardNo: string
  name: string
  role: string
}

export function readerLoginApi(cardNo: string, password: string) {
  return readerPost<ReaderLoginResponse>('/api/reader-auth/login', { cardNo, password })
}

export function readerRegisterApi(data: { cardNo: string; name: string; phone?: string; email?: string; password: string }) {
  return readerPost<ReaderLoginResponse>('/api/reader-auth/register', data)
}

export type ReaderMe = { id: number; username: string; role: string }
export function readerMeApi() {
  return readerGet<ReaderMe>('/api/reader-auth/me')
}

export function readerChangePasswordApi(oldPassword: string, newPassword: string) {
  return readerPost<void>('/api/reader-auth/change-password', { oldPassword, newPassword })
}

