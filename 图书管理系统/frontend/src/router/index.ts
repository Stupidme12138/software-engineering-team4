import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useReaderAuthStore } from '@/stores/readerAuth'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'Landing', component: () => import('@/views/LandingView.vue'), meta: { public: true } },
  { path: '/login', name: 'Login', component: () => import('@/views/LoginView.vue'), meta: { public: true } },
  { path: '/reader/login', name: 'ReaderLogin', component: () => import('@/views/reader/ReaderLoginView.vue'), meta: { public: true } },
  { path: '/reader/register', name: 'ReaderRegister', component: () => import('@/views/reader/ReaderRegisterView.vue'), meta: { public: true } },
  {
    path: '/admin',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '概览' } },
      { path: 'books', name: 'Books', component: () => import('@/views/books/BooksView.vue'), meta: { title: '图书信息' } },
      { path: 'categories', name: 'Categories', component: () => import('@/views/categories/CategoriesView.vue'), meta: { title: '图书分类' } },
      { path: 'readers', name: 'Readers', component: () => import('@/views/readers/ReadersView.vue'), meta: { title: '读者信息' } },
      { path: 'borrows', name: 'Borrows', component: () => import('@/views/borrows/BorrowsView.vue'), meta: { title: '借阅归还' } },
      { path: 'records', name: 'Records', component: () => import('@/views/records/RecordsView.vue'), meta: { title: '借阅记录' } },
      { path: 'users', name: 'Users', component: () => import('@/views/users/UsersView.vue'), meta: { title: '账号管理', roles: ['ADMIN'] } },
      { path: 'me', name: 'Me', component: () => import('@/views/MeView.vue'), meta: { title: '个人设置' } },
    ],
  },
  {
    path: '/reader',
    component: () => import('@/layouts/ReaderLayout.vue'),
    redirect: '/reader/home',
    children: [
      { path: 'home', name: 'ReaderHome', component: () => import('@/views/reader/ReaderHomeView.vue'), meta: { title: '读者首页', reader: true } },
      { path: 'books', name: 'ReaderBooks', component: () => import('@/views/reader/ReaderBooksView.vue'), meta: { title: '图书查询', reader: true } },
      { path: 'borrows', name: 'ReaderBorrows', component: () => import('@/views/reader/ReaderBorrowsView.vue'), meta: { title: '我的借阅', reader: true } },
      { path: 'me', name: 'ReaderMe', component: () => import('@/views/reader/ReaderMeView.vue'), meta: { title: '账户设置', reader: true } },
    ],
  },
]

export const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true

  // 注意：不能用 startsWith('/reader')，否则 '/readers' 也会被误判为读者端路由
  const isReaderRoute = to.path === '/reader' || to.path.startsWith('/reader/')
  if (isReaderRoute) {
    const rAuth = useReaderAuthStore()
    if (!rAuth.token) return { path: '/reader/login', query: { redirect: to.fullPath } }
    if (!rAuth.meLoaded) {
      try {
        await rAuth.fetchMe()
      } catch {
        rAuth.logout()
        return { path: '/reader/login', query: { redirect: to.fullPath } }
      }
    }
    return true
  } else {
    const auth = useAuthStore()
    if (!auth.token) return { path: '/login', query: { redirect: to.fullPath } }
    if (!auth.meLoaded) {
      try {
        await auth.fetchMe()
      } catch {
        auth.logout()
        return { path: '/login', query: { redirect: to.fullPath } }
      }
    }
    const roles = (to.meta.roles as string[] | undefined) ?? null
    if (roles && !roles.includes(auth.role ?? '')) return { path: '/admin/dashboard' }
    return true
  }
})

