package com.Sharvari.expensemanager.repository;

import com.Sharvari.expensemanager.db.DBConnection;
import com.Sharvari.expensemanager.model.Budget;
import com.Sharvari.expensemanager.model.Category;
import com.Sharvari.expensemanager.model.Expense;
import com.Sharvari.expensemanager.util.FileUtil;

import java.sql.*;
import java.util.*;
import java.sql.Date;
import java.util.stream.Collectors;


public class ExpenseRepository {

    // ─── Expense CRUD ──────────────────────────────────────────────────────────
    public List<Expense> findAllByUser(int userId) {
        String path = expenseFilePath(userId);
        FileUtil.ensureFileExists(path);
        return FileUtil.readLines(path).stream()
                .map(Expense::fromFileString)
                .collect(Collectors.toList());
    }

    public void addExpense(Expense expense) {
        String sql = """
                    Insert INTO expenses (user_id, title, amount, category, expense_date, description, payment_mode, is_recurring)
                    VALUES (?, ?, ?, ?, ?, ?,? ,?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, expense.getUserId());
            stmt.setString(2, expense.getTitle());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getCategory().name());
            stmt.setDate(5, Date.valueOf(expense.getDate()));
            stmt.setString(6, expense.getDescription());
            stmt.setString(7, expense.getPaymentMode().name());
            stmt.setBoolean(8, expense.isRecurring());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                // Note: Expense needs a setExpenseId() — add it to Expense.java
                System.out.println("✅ Expense added with ID: " + keys.getInt(1));
            }


        } catch (SQLException e) {
            System.err.println("❌ Error adding expense: " + e.getMessage());
        }
    }

    public void deleteExpense(int userId, int expenseId) {
        List<Expense> all = findAllByUser(userId);
        List<String> updated = all.stream()
                .filter(e -> e.getExpenseId() != expenseId)
                .map(Expense::toFileString)
                .collect(Collectors.toList());
        FileUtil.writeLines(expenseFilePath(userId), updated);
    }

    public void updateExpense(Expense updated) {
        List<Expense> all = findAllByUser(updated.getUserId());
        List<String> lines = all.stream()
                .map(e -> e.getExpenseId() == updated.getExpenseId() ? updated.toFileString() : e.toFileString())
                .collect(Collectors.toList());
        FileUtil.writeLines(expenseFilePath(updated.getUserId()), lines);
    }

    public int nextExpenseId(int userId) {
        return findAllByUser(userId).stream()
                .mapToInt(Expense::getExpenseId).max().orElse(0) + 1;
    }


    // ─── Budget CRUD ───────────────────────────────────────────────────────────
    public List<Budget> findBudgetsByUser(int userId) {
        String path = budgetFilePath(userId);
        FileUtil.ensureFileExists(path);
        return FileUtil.readLines(path).stream()
                .map(Budget::fromFileString)
                .collect(Collectors.toList());
    }

    public void saveBudget(Budget budget) {
        int userId = budget.getUserId();
        List<Budget> existing = findBudgetsByUser(userId);
        // Replace if same category/month/year exists
        boolean updated = false;
        List<String> lines = new ArrayList<>();
        for (Budget b : existing) {
            if (b.getCategory() == budget.getCategory()
                    && b.getMonth() == budget.getMonth()
                    && b.getYear() == budget.getYear()) {
                lines.add(budget.toFileString());
                updated = true;
            } else {
                lines.add(b.toFileString());
            }
        }
        if (!updated) lines.add(budget.toFileString());
        FileUtil.writeLines(budgetFilePath(userId), lines);
    }

    public Optional<Budget> findBudget(int userId, Category category, int month, int year) {
        return findBudgetsByUser(userId).stream()
                .filter(b -> b.getCategory() == category && b.getMonth() == month && b.getYear() == year)
                .findFirst();
    }

}
