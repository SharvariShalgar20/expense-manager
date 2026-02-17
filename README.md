# 💰 Expense Manager

> **Multi-User Console Application · Java SE · File-Based Storage**

A console-based personal finance application written in **pure Java SE** (no frameworks, no database). Multiple users can register, log in, record and categorise their expenses, set monthly budgets, and view detailed financial reports — all stored in plain `.txt` files.

Built with a clean **Model → Repository → Service** architecture designed for easy migration to **Spring Boot + Microservices** in Phase 2.

---

## 📋 Table of Contents

- [Features](#-features)
- [Project Structure](#-project-structure)
- [Data Schema & Storage](#-data-schema--storage)
- [Architecture](#-architecture)
- [Application Flow](#-application-flow)
- [Console Menu Reference](#-console-menu-reference)
- [How to Run](#-how-to-run)
- [Spring Boot Migration Roadmap](#-spring-boot-migration-roadmap)
- [Planned Features](#-planned-features)

---

## ✨ Features

- 🔐 **Multi-user** registration and login with per-user isolated data files
- ➕ Add, delete, and search expenses with full metadata
- 🏷️ **Category** and **payment mode** classification for every expense
- 🔁 **Recurring expense** flag to separate fixed vs variable costs
- 💰 Per-category **monthly budget** limits with automatic alerts at **80%** and **100%**
- 📊 **Monthly financial overview** — category breakdown, payment mode split, recurring vs one-time
- 📅 **Yearly summary** showing spend per month
- 🏆 **Top-N** most expensive transactions for any given month
- 📆 **Day-by-day** spending breakdown
- 🔍 **Keyword search** across title and description fields

---

## 📁 Project Structure

```
expense-manager/
├── src/
│   └── com/sharvari/expensemanager/
│       ├── Main.java                    ← entry point, console menu
│       ├── model/
│       │   ├── Category.java            ← expense category enum
│       │   ├── PaymentMode.java         ← payment method enum
│       │   ├── Expense.java             ← expense data class
│       │   ├── User.java                ← user data class
│       │   └── Budget.java              ← monthly budget per category
│       ├── service/
│       │   ├── UserService.java         ← register / login logic
│       │   ├── ExpenseService.java      ← add / delete / filter expenses
│       │   └── ReportService.java       ← all report generation
│       ├── repository/
│       │   ├── UserRepository.java      ← user file read/write
│       │   └── ExpenseRepository.java   ← expense & budget file read/write
│       └── util/
│           └── FileUtil.java            ← generic file I/O helpers
├── data/                                ← auto-created at runtime
│   ├── users.txt
│   ├── expenses/
│   │   └── user_{id}_expenses.txt
│   └── budgets/
│       └── user_{id}_budgets.txt
└── expense-manager.iml
```

> **Note:** The `data/` folder and all `.txt` files are created automatically when the app runs for the first time. You only need to create the `data/` directory once manually in IntelliJ.

---

## 🗄️ Data Schema & Storage

All data is stored as **pipe-delimited plain text** (`|` separator). Each record is one line. No database or external dependencies required.

### `data/users.txt`

```
userId|username|password|email|currency
```

| Field | Type | Notes |
|-------|------|-------|
| userId | int | Auto-incremented primary key |
| username | String | Unique, case-insensitive |
| password | String | Plain text (BCrypt in Spring Boot phase) |
| email | String | For future notifications |
| currency | String | 3-char ISO code e.g. `INR`, `USD` |

**Example:**
```
1|sharvari|pass123|sharvari@gmail.com|INR
2|raj|raj456|raj@gmail.com|INR
3|alice|ali789|alice@yahoo.com|USD
```

---

### `data/expenses/user_{id}_expenses.txt`

Each user has their **own isolated file**. No user can see another user's data.

```
expenseId|userId|title|amount|category|date|description|paymentMode|isRecurring
```

| Field | Type | Example | Notes |
|-------|------|---------|-------|
| expenseId | int | `1` | Auto-incremented per user |
| userId | int | `1` | Foreign key to users.txt |
| title | String | `Lunch` | Short label |
| amount | double | `150.0` | In user's chosen currency |
| category | Enum | `FOOD` | `FOOD, TRAVEL, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, OTHER` |
| date | LocalDate | `2025-06-01` | ISO format `yyyy-MM-dd` |
| description | String | `Office lunch` | Free text, searchable |
| paymentMode | Enum | `UPI` | `CASH, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, OTHER` |
| isRecurring | boolean | `false` | Marks subscriptions, EMIs, rent |

**Example:**
```
1|1|Lunch|150.0|FOOD|2025-06-01|Office lunch|UPI|false
2|1|Netflix|499.0|ENTERTAINMENT|2025-06-01|Monthly sub|CREDIT_CARD|true
3|1|Electricity Bill|1200.0|BILLS|2025-06-03|June bill|NET_BANKING|true
4|1|Uber|350.0|TRAVEL|2025-06-05|Airport drop|UPI|false
```

---

### `data/budgets/user_{id}_budgets.txt`

```
userId|category|month|year|limit
```

| Field | Type | Example | Notes |
|-------|------|---------|-------|
| userId | int | `1` | FK to user |
| category | Enum | `FOOD` | One budget per category per month |
| month | int | `6` | Calendar month 1–12 |
| year | int | `2025` | 4-digit year |
| limit | double | `3000.0` | Max spend allowed |

**Example:**
```
1|FOOD|6|2025|3000.0
1|ENTERTAINMENT|6|2025|500.0
1|TRAVEL|6|2025|2000.0
```

> Setting a budget for the same `category + month + year` again **overwrites** the previous value (upsert behaviour).

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────┐
│                   Main.java                      │
│           (Console UI / User Input)              │
└─────────────────────┬────────────────────────────┘
                      │ calls
┌─────────────────────▼────────────────────────────┐
│              Service Layer                       │
│   UserService  │  ExpenseService  │  ReportService│
└─────────────────────┬────────────────────────────┘
                      │ calls
┌─────────────────────▼────────────────────────────┐
│            Repository Layer                      │
│       UserRepository  │  ExpenseRepository       │
└─────────────────────┬────────────────────────────┘
                      │ reads/writes via FileUtil
┌─────────────────────▼────────────────────────────┐
│         data/ (plain-text .txt files)            │
│  users.txt │ user_N_expenses.txt │ user_N_budgets │
└──────────────────────────────────────────────────┘
```

### Class Responsibilities

| Class | Responsibility |
|-------|---------------|
| `User` | Holds user profile data. Implements `toFileString()` / `fromFileString()` for serialisation |
| `Expense` | All fields for a single expense entry including `paymentMode` and `isRecurring` |
| `Budget` | Spending cap: `userId + category + month + year + limit` |
| `Category` | Enum: `FOOD, TRAVEL, SHOPPING, BILLS, ENTERTAINMENT, HEALTH, EDUCATION, OTHER` |
| `PaymentMode` | Enum: `CASH, CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, OTHER` |
| `UserRepository` | Loads all users on startup. Handles find, add, save operations on `users.txt` |
| `ExpenseRepository` | Manages per-user expense and budget files. Full CRUD + budget upsert |
| `UserService` | Registration (uniqueness check) and login (credential matching) |
| `ExpenseService` | Add/delete/filter expenses, set budgets, trigger budget alerts |
| `ReportService` | Monthly overview, yearly summary, top-N, daily breakdown, recurring list |
| `FileUtil` | `readLines()`, `writeLines()`, `appendLine()`, `ensureFileExists()` — all file I/O lives here |

---

## 🔄 Application Flow

### Authentication
```
App starts → Auth Menu
  ├── Register → username + password + email + currency
  │              → check uniqueness → assign userId → save to users.txt → auto-login
  │
  ├── Login    → username + password → scan users.txt → match → load currentUser
  │
  └── Exit
```

### Adding an Expense
```
Add Expense selected
  → enter: title, amount, category, date (blank = today),
           description, paymentMode, recurring (y/n)
  → ExpenseService.addExpense()
       → nextExpenseId = highest existing id + 1
       → build Expense object
       → append to user_{id}_expenses.txt
       → checkBudgetAlert()
            → find budget for category/month/year
            → calculate % spent
            → ⚠️  WARNING printed at 80%
            → 🚨 ALERT printed at 100%
```

### Monthly Overview
```
Monthly Overview selected
  → enter MM/yyyy (blank = current month)
  → load all expenses for that month
  → group by Category  → show amount + % + budget status
  → group by PaymentMode → show amount
  → split Recurring vs One-time
  → print total
  → print Top 5 expenses
  → print day-by-day breakdown
```

---

## 📟 Console Menu Reference

### Auth Menu
| # | Option | Description |
|---|--------|-------------|
| 1 | Login | Authenticate with username + password |
| 2 | Register | Create a new account with email and currency |
| 3 | Exit | Terminate the application |

### Main Menu (after login)
| # | Option | Description |
|---|--------|-------------|
| 1 | Add Expense | Record a new expense; triggers budget alert |
| 2 | View All Expenses | List all expenses newest-first with total |
| 3 | Delete Expense | Show list then prompt for ID to remove |
| 4 | Search Expenses | Keyword search across title and description |
| 5 | Filter by Category | Show all expenses in a chosen category with subtotal |
| 6 | Monthly Overview | Full report: category %, payment mode, recurring split, top 5, daily breakdown |
| 7 | Yearly Summary | Month-by-month total for a chosen year |
| 8 | Set / View Budget | Set a category monthly limit or view all budgets |
| 9 | View Recurring Expenses | List all recurring expenses across all time |
| 10 | Top Expenses This Month | Highest-value N expenses for current month |
| 11 | Daily Breakdown | Aggregated spend per day for a chosen month |
| 12 | Logout | Return to Auth menu |

---

## ▶️ How to Run

**Requirements:** JDK 11 or higher. No Maven, no Gradle, no dependencies.

```bash
# 1. Clone the repo
git clone https://github.com/SharvariShalgar20/expense-manager.git

# 2. Open in IntelliJ IDEA

# 3. Create the data folder at project root
#    Right-click project → New → Directory → name it: data

# 4. Run Main.java
#    All subfolders and .txt files are created automatically
```

---

## 🚀 Spring Boot Migration Roadmap

The layered architecture means migrating to Spring Boot requires changing **only the repository layer**. Service and model classes stay intact.

| Current (Console Java) | Spring Boot Equivalent | Change Required |
|------------------------|----------------------|-----------------|
| `model/User.java` | `@Entity User.java` | Add `@Entity`, `@Id`, `@Column` |
| `model/Expense.java` | `@Entity Expense.java` | Add `@Entity`, `@ManyToOne(User)` |
| `model/Budget.java` | `@Entity Budget.java` | Add `@Entity`, composite key |
| `UserRepository.java` | `extends JpaRepository` | Replace file logic with JPA interface |
| `ExpenseRepository.java` | `extends JpaRepository` | Replace file logic with JPA interface |
| `UserService.java` | `@Service` + BCrypt | Add `@Service`, hash passwords |
| `ExpenseService.java` | `@Service` | Add `@Service`, `@Transactional` |
| `ReportService.java` | `@Service` | Add `@Service` annotation only |
| `Main.java` console menu | `@RestController` classes | Replace `Scanner` with HTTP endpoints |
| Auth menu | Spring Security + JWT | Add security config, JWT filter |
| `FileUtil.java` | *(deleted)* | Not needed with JPA |

---

## 🗺️ Planned Features

### Phase 2 — Spring Boot REST API
- Replace file storage with MySQL / PostgreSQL via Spring Data JPA
- JWT-based authentication with BCrypt password hashing
- REST endpoints for all current menu operations
- Input validation with Bean Validation (`@NotNull`, `@Positive`, etc.)
- Swagger / OpenAPI auto-generated documentation

### Phase 3 — Microservices
- **User Service** — registration, login, profile
- **Expense Service** — CRUD for expenses
- **Report Service** — analytics and financial overview
- **Budget Service** — budget management and alerts
- **API Gateway** — routing, rate limiting
- Service discovery via Eureka

### Phase 4 — Enhancements
- Income tracking (net balance = income − expenses)
- Receipt image upload and OCR
- Email / push notifications on budget breach
- CSV / PDF data export
- Web or mobile front end (React / Android)

---

## 👩‍💻 Author

**Sharvari Shalgar**
[github.com/SharvariShalgar20](https://github.com/SharvariShalgar20)