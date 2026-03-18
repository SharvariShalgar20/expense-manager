-- 1. Create DataBase

CREATE DATABASE IF NOT EXISTS expense_manager
       CHARACTER SET utf8mb4
       COLLATE utf8mb4_unicode_ci;

USE expense_manager;


-- 2. Create Tables

CREATE TABLE IF NOT EXISTS users (
    user_id    INT           NOT NULL AUTO_INCREMENT,
    sername   VARCHAR(50)    NOT NULL UNIQUE,
    password   VARCHAR(255)  NOT NULL,
    email      VARCHAR(100)  NOT NULL UNIQUE,
    currency   VARCHAR(10)   NOT NULL DEFAULT 'INR',
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
    );


CREATE TABLE IF NOT EXISTS expenses (
    expense_id   INT            NOT NULL AUTO_INCREMENT,
    user_id      INT            NOT NULL,
    title        VARCHAR(100)   NOT NULL,
    amount       DECIMAL(10,2)  NOT NULL CHECK (amount > 0),
    category     VARCHAR(30)    NOT NULL,
    expense_date DATE           NOT NULL,
    description  VARCHAR(255)   DEFAULT '',
    payment_mode VARCHAR(30)    NOT NULL,
    is_recurring BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (expense_id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id)
    REFERENCES users(user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS budgets (
    budget_id    INT           NOT NULL AUTO_INCREMENT,
    user_id      INT           NOT NULL,
    category     VARCHAR(30)   NOT NULL,
    month        INT           NOT NULL CHECK (month BETWEEN 1 AND 12),
    year         INT           NOT NULL,
    limit_amount DECIMAL(10,2) NOT NULL CHECK (limit_amount > 0),
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (budget_id),
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id)
    REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT uq_budget UNIQUE (user_id, category, month, year)
    );


-- 3. Indexes

CREATE INDEX idx_expenses_user     ON expenses(user_id);
CREATE INDEX idx_expenses_date     ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_budgets_user      ON budgets(user_id);


-- 4. Common Application Queries

-- Get all expenses for a user (newest first)
SELECT * FROM expenses
WHERE user_id = ?
ORDER BY expense_date DESC;

-- Get expenses for a specific month/year
SELECT * FROM expenses
WHERE user_id = ?
    AND MONTH(expense_date) = ?
    AND YEAR(expense_date) = ?
    ORDER BY expense_date ASC;

-- Get expenses by category
SELECT * FROM expenses
WHERE user_id = ? AND category = ?;

-- Search expenses by keyword in title or description
SELECT * FROM expenses
WHERE user_id = ?
    AND (LOWER(title) LIKE CONCAT('%', LOWER(?), '%')
    OR LOWER(description) LIKE CONCAT('%', LOWER(?), '%'));

-- Get total spending per category for a month
SELECT category, SUM(amount) AS total
FROM expenses
WHERE user_id = ?
    AND MONTH(expense_date) = ?
    AND YEAR(expense_date) = ?
    GROUP BY category
    ORDER BY total DESC;


-- Get monthly totals for a year( yearly summary )
SELECT MONTH(expense_date) AS month, SUM(amount) AS total
FROM expenses
WHERE user_id = ? AND YEAR(expense_date) = ?
GROUP BY MONTH(expense_date)
ORDER BY month;


-- Get top N expenses for a month
SELECT * FROM expenses
WHERE user_id = ?
    AND MONTH(expense_date) = ?
    AND YEAR(expense_date) = ?
ORDER BY amount DESC LIMIT ?;


-- Get all recurring expenses for a user
SELECT * FROM expenses
WHERE user_id = ? AND is_recurring = TRUE;


-- Get daily spending for a month
SELECT expense_date AS date, SUM(amount) AS daily_total
FROM expenses
WHERE user_id = ?
    AND MONTH(expense_date) = ?
  AND YEAR(expense_date) = ?
GROUP BY expense_date
ORDER BY date;



-- 5. Budget Queries

-- Upsert a budget
INSERT INTO budgets (user_id, category, month, year, limit_amount)
VALUES (?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE limit_amount = VALUES(limit_amount);


-- Get a specific budget
SELECT * FROM budgets
WHERE user_id = ? AND category = ? AND month = ? AND year = ?;


-- Get all budgets for a user
SELECT * FROM budgets WHERE user_id = ? ORDER BY year, month


-- Budget vs actual spending (join query)
SELECT
    b.category,
    b.limit_amount,
    COALESCE(SUM(e.amount), 0)               AS spent,
    b.limit_amount - COALESCE(SUM(e.amount), 0) AS remaining,
    ROUND(COALESCE(SUM(e.amount), 0) / b.limit_amount * 100, 1) AS pct_used
FROM budgets b
         LEFT JOIN expenses e
                   ON  e.user_id  = b.user_id
                       AND e.category = b.category
                       AND MONTH(e.expense_date) = b.month
    AND YEAR(e.expense_date)  = b.year
WHERE b.user_id = ? AND b.month = ? AND b.year = ?
GROUP BY b.category, b.limit_amount;

