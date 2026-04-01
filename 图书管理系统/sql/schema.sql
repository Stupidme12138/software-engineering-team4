-- MySQL 5.7+/8.0+（兼容：避免使用 utf8mb4_0900_ai_ci）
-- 图书管理系统：建表 + 索引

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 如你想一键建库，可取消下面两行注释
-- CREATE DATABASE IF NOT EXISTS library_ms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE library_ms;

-- =========================
-- 用户与权限（简化：角色枚举）
-- =========================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  username        VARCHAR(64)  NOT NULL UNIQUE,
  password_hash   VARCHAR(100) NOT NULL,
  role            VARCHAR(20)  NOT NULL, -- ADMIN / LIBRARIAN
  enabled         TINYINT(1)   NOT NULL DEFAULT 1,
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 读者信息
-- =========================
DROP TABLE IF EXISTS reader;
CREATE TABLE reader (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  card_no       VARCHAR(32)  NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  name          VARCHAR(64)  NOT NULL,
  phone         VARCHAR(32)  NULL,
  email         VARCHAR(128) NULL,
  id_card       VARCHAR(32)  NULL,
  status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE', -- ACTIVE / CANCELLED
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_reader_name (name),
  INDEX idx_reader_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 图书分类
-- =========================
DROP TABLE IF EXISTS book_category;
CREATE TABLE book_category (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(64) NOT NULL UNIQUE,
  subject     VARCHAR(64) NULL,         -- 学科（可选）
  description VARCHAR(255) NULL,
  created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_category_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 图书信息
-- =========================
DROP TABLE IF EXISTS book;
CREATE TABLE book (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  isbn            VARCHAR(32)  NULL UNIQUE,
  title           VARCHAR(255) NOT NULL,
  author          VARCHAR(128) NULL,
  publisher       VARCHAR(128) NULL,
  publish_date    DATE         NULL,
  category_id     BIGINT       NULL,
  subject         VARCHAR(64)  NULL,
  shelf_location  VARCHAR(64)  NULL, -- 书架位置
  total_qty       INT          NOT NULL DEFAULT 1,
  available_qty   INT          NOT NULL DEFAULT 1,
  status          VARCHAR(20)  NOT NULL DEFAULT 'ENABLED', -- ENABLED / DISABLED
  created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES book_category(id),
  INDEX idx_book_title (title),
  INDEX idx_book_author (author),
  INDEX idx_book_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 借阅记录
-- =========================
DROP TABLE IF EXISTS borrow_record;
CREATE TABLE borrow_record (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  reader_id        BIGINT      NOT NULL,
  book_id          BIGINT      NOT NULL,
  borrow_time      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  due_time         DATETIME    NULL,
  return_time      DATETIME    NULL,
  status           VARCHAR(20) NOT NULL, -- BORROWED / RETURNED
  operator_user_id BIGINT      NULL,     -- 操作员（馆员/管理员）
  remark           VARCHAR(255) NULL,
  created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_borrow_reader FOREIGN KEY (reader_id) REFERENCES reader(id),
  CONSTRAINT fk_borrow_book   FOREIGN KEY (book_id) REFERENCES book(id),
  CONSTRAINT fk_borrow_op     FOREIGN KEY (operator_user_id) REFERENCES sys_user(id),
  INDEX idx_borrow_reader_time (reader_id, borrow_time),
  INDEX idx_borrow_book_time (book_id, borrow_time),
  INDEX idx_borrow_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- 说明：
-- 1) 默认管理员账号会在后端启动时自动创建（若 sys_user 为空）。
-- 2) 如需手工插入管理员，请使用 BCrypt 生成 password_hash（长度约 60）。

