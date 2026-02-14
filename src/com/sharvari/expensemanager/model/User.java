package com.sharvari.expensemanager.model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Long userId;
    private String name;
    private String email;
    private List<Expense> expenses;


    public User(){
        this.expenses = new ArrayList<>();
    }

    public User(Long userId, String name, String email){
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.expenses = new ArrayList<>();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    // Add Expense
    public void addExpense(Expense expense) {
        this.expenses.add(expense);
    }

    // Remove Expense
    public void removeExpense(Expense expense) {
        this.expenses.remove(expense);
    }

    // Get All Expenses
    public List<Expense> getExpenses() {
        return expenses;
    }
}
