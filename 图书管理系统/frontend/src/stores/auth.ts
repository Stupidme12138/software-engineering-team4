import { defineStore } from 'pinia'
import { loginApi, meApi, type Me } from '@/api/auth'

const TOKEN_KEY = 'library_ms_token'
const ME_KEY = 'library_ms_me'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) as string | null,
    me: (localStorage.getItem(ME_KEY) ? (JSON.parse(localStorage.getItem(ME_KEY)!) as Me) : null) as Me | null,
    meLoaded: false,
  }),
  getters: {
    username: (s) => s.me?.username ?? null,
    role: (s) => s.me?.role ?? null,
    userId: (s) => s.me?.id ?? null,
    isAdmin: (s) => s.me?.role === 'ADMIN',
  },
  actions: {
    async login(username: string, password: string) {
      const res = await loginApi(username, password)
      this.token = res.accessToken
      localStorage.setItem(TOKEN_KEY, res.accessToken)
      this.me = { id: res.userId, username: res.username, role: res.role }
      localStorage.setItem(ME_KEY, JSON.stringify(this.me))
      this.meLoaded = true
    },
    async fetchMe() {
      const m = await meApi()
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

