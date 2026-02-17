package com.sharvari.expensemanager.model;

import java.time.LocalDate;

public class Expense {

    private int expenseId;
    private int userId;           // ← links expense to user
    private String title;
    private double amount;
    private Category category;
    private LocalDate date;
    private String description;
    private PaymentMode paymentMode;
    private boolean isRecurring;

    public Expense() {}

    public Expense(int expenseId, int userId, String title, double amount,
                   Category category, LocalDate date, String description,
                   PaymentMode paymentMode, boolean isRecurring) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.paymentMode = paymentMode;
        this.isRecurring = isRecurring;
    }

    public int getExpenseId() { return expenseId; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public PaymentMode getPaymentMode() { return paymentMode; }
    public boolean isRecurring() { return isRecurring; }


    public String toFileString() {
        return expenseId + "|" + userId + "|" + title + "|" + amount + "|"
                + category + "|" + date + "|" + description + "|"
                + paymentMode + "|" + isRecurring;
    }


    public static Expense fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Expense(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                p[2],
                Double.parseDouble(p[3]),
                Category.valueOf(p[4]),
                LocalDate.parse(p[5]),
                p[6],
                PaymentMode.valueOf(p[7]),
                Boolean.parseBoolean(p[8])
        );
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s | %-12s | %8.2f | %-12s | %s | %s | Recurring: %s",
                expenseId, title, category, amount, paymentMode, date, description, isRecurring);
    }
}
