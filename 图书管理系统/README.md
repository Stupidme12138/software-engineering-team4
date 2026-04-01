# 图书管理系统（Vue3 + Spring Boot 3 + MySQL）

## 目录结构

- `frontend/`：前端（Vue3 + Vite + Element Plus）
- `backend/`：后端（Spring Boot 3 + Maven + MyBatis + MySQL + JWT）
- `sql/`：数据库建表与初始化脚本

## 运行前准备

- Node.js 18+（建议 20 LTS）
- JDK 17+ (我用的jdk21，应该换成1.8也行)
- Maven 3.9+
- MySQL 5.7+（或 8.0+）

## 数据库初始化

1. 创建数据库（例如：`library_ms`）
2. 执行 `sql/schema.sql`

> 默认管理员账号由后端启动时自动创建（若 `sys_user` 为空）：admin / Admin@123456

## 后端启动（backend）

1. 修改 `backend/src/main/resources/application-dev.yml` 里的数据库连接账号密码
2. 启动：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端默认地址：`http://localhost:8080`

## 前端启动（frontend）

```bash
npm install
npm run dev
```

前端开发地址：`http://localhost:5173`

打开后先进入入口选择页：`/`（可选择进入管理端或读者端）

## 默认账号

- 管理员：admin / Admin@123456
- 读者：可在读者端自行注册（或由后台新增，默认初始密码 Reader@123456）

