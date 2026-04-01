import { defineStore } from 'pinia'
import { readerLoginApi, readerMeApi, type ReaderMe } from '@/api/readerAuth'

const TOKEN_KEY = 'library_ms_reader_token'
const ME_KEY = 'library_ms_reader_me'

export const useReaderAuthStore = defineStore('readerAuth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) as string | null,
    me: (localStorage.getItem(ME_KEY) ? (JSON.parse(localStorage.getItem(ME_KEY)!) as ReaderMe) : null) as ReaderMe | null,
    meLoaded: false,
  }),
  getters: {
    cardNo: (s) => s.me?.username ?? null,
    role: (s) => s.me?.role ?? null,
    readerId: (s) => s.me?.id ?? null,
    isReader: (s) => s.me?.role === 'READER',
  },
  actions: {
    async login(cardNo: string, password: string) {
      const res = await readerLoginApi(cardNo, password)
      this.token = res.accessToken
      localStorage.setItem(TOKEN_KEY, res.accessToken)
      // 后端的 /api/reader-auth/me 返回 AuthPrincipal: {id, username, role}
      this.me = { id: res.readerId, username: res.cardNo, role: res.role }
      localStorage.setItem(ME_KEY, JSON.stringify(this.me))
      this.meLoaded = true
    },
    async fetchMe() {
      const m = await readerMeApi()
      this.me = m
      localStorage.setItem(ME_KEY, JSON.stringify(m))
      this.meLoaded = true
    },
    logout() {
      this.token = null
      this.me = null
      this.meLoaded = false
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(ME_KEY)
    },
  },
})

