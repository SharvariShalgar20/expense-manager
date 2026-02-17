package com.sharvari.expensemanager.service;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final ExpenseService expenseService;
    private final ExpenseRepository repo;

    public ReportService(ExpenseService expenseService) {
        this.expenseService = expenseService;
        this.repo = expenseService.getRepo();
    }

    // ── Monthly Financial Overview ─────────────────────────────────────────────
    public void printMonthlyOverview(int userId, int month, int year, String currency) {
        List<Expense> expenses = expenseService.getByMonth(userId, month, year);
        if (expenses.isEmpty()) {
            System.out.println("No expenses for " + Month.of(month) + " " + year);
            return;
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        System.out.println("\n" + "=".repeat(55));
        System.out.printf("  Monthly Overview: %s %d | Currency: %s%n", Month.of(month), year, currency);
        System.out.println("=".repeat(55));

        // By category
        Map<Category, Double> byCategory = new LinkedHashMap<>();
        for (Category cat : Category.values()) {
            double sum = expenses.stream()
                    .filter(e -> e.getCategory() == cat)
                    .mapToDouble(Expense::getAmount).sum();
            if (sum > 0) byCategory.put(cat, sum);
        }

        System.out.println("\n  Spending by Category:");
        byCategory.entrySet().stream()
                .sorted(Map.Entry.<Category, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    double pct = (entry.getValue() / total) * 100;
                    // Budget status
                    String budgetStatus = "";
                    Optional<Budget> bOpt = repo.findBudget(userId, entry.getKey(), month, year);
                    if (bOpt.isPresent()) {
                        double limit = bOpt.get().getLimit();
                        double used = (entry.getValue() / limit) * 100;
                        budgetStatus = String.format(" [Budget: %.2f | Used: %.0f%%]", limit, used);
                    }
                    System.out.printf("  %-15s : %8.2f  (%5.1f%%)%s%n",
                            entry.getKey(), entry.getValue(), pct, budgetStatus);
                });

        // By payment mode
        System.out.println("\n  Spending by Payment Mode:");
        Arrays.stream(PaymentMode.values()).forEach(mode -> {
            double sum = expenses.stream()
                    .filter(e -> e.getPaymentMode() == mode)
                    .mapToDouble(Expense::getAmount).sum();
            if (sum > 0) System.out.printf("  %-15s : %8.2f%n", mode, sum);
        });

        // Recurring vs one-time
        double recurring = expenses.stream().filter(Expense::isRecurring).mapToDouble(Expense::getAmount).sum();
        double oneTime   = total - recurring;

        System.out.printf("%n  Recurring Expenses : %8.2f%n", recurring);
        System.out.printf("  One-time Expenses  : %8.2f%n", oneTime);
        System.out.println("-".repeat(55));
        System.out.printf("  TOTAL              : %8.2f %s%n", total, currency);
        System.out.println("=".repeat(55));
    }

    // ── Top N Expenses ─────────────────────────────────────────────────────────
    public void printTopExpenses(int userId, int month, int year, int n) {
        List<Expense> top = expenseService.getByMonth(userId, month, year).stream()
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed())
                .limit(n)
                .collect(Collectors.toList());

        System.out.println("\n  Top " + n + " Expenses:");
        top.forEach(e -> System.out.println("  " + e));
    }

    // ── Yearly Summary ─────────────────────────────────────────────────────────
    public void printYearlySummary(int userId, int year, String currency) {
        List<Expense> all = expenseService.getAllExpenses(userId).stream()
                .filter(e -> e.getDate().getYear() == year)
                .collect(Collectors.toList());

        System.out.println("\n" + "=".repeat(55));
        System.out.printf("  Yearly Summary: %d | Currency: %s%n", year, currency);
        System.out.println("=".repeat(55));

        double yearTotal = 0;
        for (int m = 1; m <= 12; m++) {
            final int fm = m;
            double mTotal = all.stream()
                    .filter(e -> e.getDate().getMonthValue() == fm)
                    .mapToDouble(Expense::getAmount).sum();
            if (mTotal > 0) {
                System.out.printf("  %-12s : %10.2f%n", Month.of(m), mTotal);
                yearTotal += mTotal;
            }
        }
        System.out.println("-".repeat(55));
        System.out.printf("  TOTAL          : %10.2f %s%n", yearTotal, currency);
        System.out.println("=".repeat(55));
    }

    // ── Day-wise for current month ─────────────────────────────────────────────
    public void printDailyBreakdown(int userId, int month, int year) {
        Map<LocalDate, Double> daily = new TreeMap<>();
        expenseService.getByMonth(userId, month, year).forEach(e ->
                daily.merge(e.getDate(), e.getAmount(), Double::sum)
        );

        System.out.println("\n  Daily Breakdown:");
        daily.forEach((date, amount) ->
                System.out.printf("  %s : %.2f%n", date, amount)
        );
    }

    // ── Recurring Expenses List ────────────────────────────────────────────────
    public void printRecurringExpenses(int userId) {
        List<Expense> recurring = expenseService.getAllExpenses(userId).stream()
                .filter(Expense::isRecurring)
                .collect(Collectors.toList());
        System.out.println("\n  Recurring Expenses:");
        if (recurring.isEmpty()) System.out.println("  None found.");
        else recurring.forEach(e -> System.out.println("  " + e));
    }
}
