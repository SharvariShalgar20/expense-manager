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

