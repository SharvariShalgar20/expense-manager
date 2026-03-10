package com.sharvari.expensemanager.service;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.repository.ExpenseRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseService() {
        this.repo = new ExpenseRepository();
    }

    public void addExpense(int userId, String title, double amount, Category category,
                           LocalDate date, String description, PaymentMode paymentMode, boolean recurring) {

        if (amount <= 0) {
            System.out.println("❌ Amount must be greater than 0.");
            return;
        }
        if (title == null || title.trim().isEmpty()) {
            System.out.println("❌ Title cannot be empty.");
            return;
        }

        int id = repo.nextExpenseId(userId);
        Expense expense = new Expense(id, userId, title.trim(), amount, category, date, description, paymentMode, recurring);
        repo.addExpense(expense);
        System.out.println("✅ Expense added! ID: " + id);

        // Budget alert check
        checkBudgetAlert(userId, category, date.getMonthValue(), date.getYear());
    }

    public void updateExpense(int userId, int expenseId, String title, double amount,
                              Category category, LocalDate date, String description,
                              PaymentMode paymentMode, boolean recurring) {
        if (amount <= 0) {
            System.out.println("❌ Amount must be greater than 0.");
            return;
        }
        Expense updated = new Expense(expenseId, userId, title.trim(), amount,
                category, date, description, paymentMode, recurring);
        repo.updateExpense(updated);
        System.out.println("✅ Expense " + expenseId + " updated.");
        checkBudgetAlert(userId, category, date.getMonthValue(), date.getYear());
    }

    public void deleteExpense(int userId, int expenseId) {

        boolean exists = repo.findAllByUser(userId).stream().anyMatch(e -> e.getExpenseId() == expenseId);
        if (!exists) {
            System.out.println("❌ Expense ID " + expenseId + " not found.");
            return;
        }
        repo.deleteExpense(userId, expenseId);
        System.out.println("🗑️ Expense " + expenseId + " deleted.");
    }

    public List<Expense> getAllExpenses(int userId) {
        return repo.findAllByUser(userId).stream()
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Expense> getByMonth(int userId, int month, int year) {
        return repo.findAllByUser(userId).stream()
                .filter(e -> e.getDate().getMonthValue() == month && e.getDate().getYear() == year)
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }

    public List<Expense> getByCategory(int userId, Category category) {
        return repo.findAllByUser(userId).stream()
                .filter(e -> e.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Expense> searchByKeyword(int userId, String keyword) {
        String kw = keyword.toLowerCase();
        return repo.findAllByUser(userId).stream()
                .filter(e -> e.getTitle().toLowerCase().contains(kw)
                        || e.getDescription().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    // ─── Budget ────────────────────────────────────────────────────────────────

    public void setBudget(int userId, Category category, int month, int year, double limit) {
        Budget budget = new Budget(userId, category, month, year, limit);
        repo.saveBudget(budget);
        System.out.printf("✅ Budget set: %s → %.2f for %02d/%d%n", category, limit, month, year);
    }

    public void checkBudgetAlert(int userId, Category category, int month, int year) {
        repo.findBudget(userId, category, month, year).ifPresent(budget -> {
            double spent = getByMonth(userId, month, year).stream()
                    .filter(e -> e.getCategory() == category)
                    .mapToDouble(Expense::getAmount).sum();
            double pct = (spent / budget.getLimit()) * 100;
            if (pct >= 100) {
                System.out.printf("🚨 ALERT: %s budget EXCEEDED! Spent %.2f / %.2f (%.0f%%)%n",
                        category, spent, budget.getLimit(), pct);
            } else if (pct >= 80) {
                System.out.printf("⚠️  WARNING: %s budget at %.0f%% (%.2f / %.2f)%n",
                        category, pct, spent, budget.getLimit());
            }
        });
    }

    public ExpenseRepository getRepo() { return repo; }
}
