# Expense Manager — Database Schema

## Overview
Relational schema for the Expense Manager app.
Designed for MySQL.
This replaces the flat .txt file storage used in Phase 1.

---

## Tables

### 1. users
Stores registered users.

| Column      | Type           | Constraints                  |
|-------------|----------------|------------------------------|
| user_id     | INT            | PRIMARY KEY, AUTO_INCREMENT  |
| username    | VARCHAR(50)    | NOT NULL, UNIQUE             |
| password    | VARCHAR(255)   | NOT NULL                     |
| email       | VARCHAR(100)   | NOT NULL, UNIQUE             |
| currency    | VARCHAR(10)    | NOT NULL, DEFAULT 'INR'      |
| created_at  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP    |

---

### 2. expenses
Stores each expense entry linked to a user.

| Column       | Type           | Constraints                          |
|--------------|----------------|--------------------------------------|
| expense_id   | INT            | PRIMARY KEY, AUTO_INCREMENT          |
| user_id      | INT            | NOT NULL, FOREIGN KEY → users(user_id)|
| title        | VARCHAR(100)   | NOT NULL                             |
| amount       | DECIMAL(10,2)  | NOT NULL, CHECK (amount > 0)         |
| category     | VARCHAR(30)    | NOT NULL                             |
| expense_date | DATE           | NOT NULL                             |
| description  | VARCHAR(255)   | DEFAULT ''                           |
| payment_mode | VARCHAR(30)    | NOT NULL                             |
| is_recurring | BOOLEAN        | DEFAULT FALSE                        |
| created_at   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP            |

---

### 3. budgets
Stores monthly budget limits per category per user.

| Column      | Type           | Constraints                           |
|-------------|----------------|---------------------------------------|
| budget_id   | INT            | PRIMARY KEY, AUTO_INCREMENT           |
| user_id     | INT            | NOT NULL, FOREIGN KEY → users(user_id)|
| category    | VARCHAR(30)    | NOT NULL                              |
| month       | INT            | NOT NULL, CHECK (month BETWEEN 1-12)  |
| year        | INT            | NOT NULL                              |
| limit_amount| DECIMAL(10,2)  | NOT NULL, CHECK (limit_amount > 0)    |
| created_at  | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP             |

UNIQUE constraint: (user_id, category, month, year)
→ One budget per category per month per user.

---

## Relationships

users  ──< expenses   (one user → many expenses)
users  ──< budgets    (one user → many budgets)

---

## Enum Values (stored as VARCHAR)

### Category
FOOD, TRAVEL, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, OTHER

### PaymentMode
CASH, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, OTHER