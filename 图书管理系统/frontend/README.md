# 前端（Vue3 + Element Plus）

## 启动

```bash
npm install
npm run dev
```

默认开发地址：`http://localhost:5173`

## 后端代理

开发环境下，`vite.config.ts` 已将 `/api` 代理到 `http://localhost:8080`。

## 登录

- 默认管理员：admin / Admin@123456
- 读者：可在读者端注册自建账号（或由后台新增，默认 Reader@123456）

## 页面

- `/`：入口选择（管理端/读者端）
- `/admin/**`：管理端（后台）
  - `/admin/dashboard`：概览（统计卡片）
  - `/admin/books`：图书信息（查询/分页/新增/编辑/删除）
  - `/admin/categories`：分类管理（查询/分页/新增/编辑/删除）
  - `/admin/readers`：读者管理（查询/分页/登记/编辑/注销 + 借阅历史）
  - `/admin/borrows`：借阅/归还（仅馆员显示入口）
  - `/admin/records`：借阅记录查询
  - `/admin/users`：账号管理（仅 ADMIN 可见）
  - `/admin/me`：个人设置（修改密码）
- `/reader/**`：读者端（前台）
  - `/reader/login`：读者登录
  - `/reader/register`：读者注册
  - `/reader/home`：读者首页
  - `/reader/books`：图书查询 + 借阅
  - `/reader/borrows`：我的借阅 + 归还
  - `/reader/me`：账户设置（修改密码）

