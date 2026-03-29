-- ============================================================
--  Library Management System — MySQL 8.0 Schema
--  Run this inside Docker container: mysql-lab
--  Host: 127.0.0.1 | Port: 3306 | User: root | Pass: root
-- ============================================================

CREATE DATABASE IF NOT EXISTS lms_db;
USE lms_db;

-- ── Users Table ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS lms_users (
    user_id     VARCHAR(20)  PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    password    VARCHAR(64)  NOT NULL,        -- SHA-256 hex (64 chars)
    role        ENUM('ADMIN','USER') DEFAULT 'USER',
    balance     DECIMAL(10,2) DEFAULT 0.00,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Books Table ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS lms_books (
    book_id          VARCHAR(20)   PRIMARY KEY,
    title            VARCHAR(200)  NOT NULL,
    author           VARCHAR(100)  NOT NULL,
    genre            VARCHAR(50),
    total_copies     INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    price_per_day    DECIMAL(6,2) DEFAULT 1.00
);

-- ── Borrow Records Table ────────────────────────────────────
CREATE TABLE IF NOT EXISTS lms_borrows (
    borrow_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(20) REFERENCES lms_users(user_id),
    book_id     VARCHAR(20) REFERENCES lms_books(book_id),
    borrow_date DATE    DEFAULT (CURDATE()),
    due_date    DATE,
    return_date DATE,
    fine_amount DECIMAL(8,2) DEFAULT 0.00,
    status      ENUM('ACTIVE','RETURNED') DEFAULT 'ACTIVE'
);

-- ── Seed Admin Account ──────────────────────────────────────
-- password: admin  (SHA-256)
INSERT IGNORE INTO lms_users(user_id, name, email, password, role, balance)
VALUES (
    'ADMIN001',
    'Librarian Admin',
    'admin@lms.com',
    '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918',
    'ADMIN',
    9999.00
);

-- ── Sample Books ────────────────────────────────────────────
INSERT IGNORE INTO lms_books(book_id, title, author, genre, total_copies, price_per_day)
VALUES
    ('B001', 'Clean Code',               'Robert C. Martin', 'Programming',  3, 2.00),
    ('B002', 'The Pragmatic Programmer', 'Andrew Hunt',      'Programming',  2, 2.50),
    ('B003', 'Introduction to Algorithms','CLRS',            'CS Theory',    4, 3.00),
    ('B004', 'Design Patterns',          'GoF',              'Architecture', 2, 2.00),
    ('B005', 'You Don''t Know JS',       'Kyle Simpson',     'JavaScript',   3, 1.50);

COMMIT;
