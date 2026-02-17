package com.sharvari.expensemanager.repository;

import com.sharvari.expensemanager.model.Budget;
import com.sharvari.expensemanager.model.Category;
import com.sharvari.expensemanager.model.Expense;
import com.sharvari.expensemanager.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;


public class ExpenseRepository {

    private String expenseFilePath(int userId) {
        return "data/expenses/user_" + userId + "_expenses.txt";
    }

    private String budgetFilePath(int userId) {
        return "data/budgets/user_" + userId + "_budgets.txt";
    }

    // ─── Expense CRUD ──────────────────────────────────────────────────────────
    public List<Expense> findAllByUser(int userId) {
        String path = expenseFilePath(userId);
        FileUtil.ensureFileExists(path);
        return FileUtil.readLines(path).stream()
                .map(Expense::fromFileString)
                .collect(Collectors.toList());
    }

    public void addExpense(Expense expense) {
        FileUtil.appendLine(expenseFilePath(expense.getUserId()), expense.toFileString());
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
