package com.sharvari.expensemanager.service;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.util.FileHandler;

import java.util.ArrayList;
import java.util.List;


public class ExpenseManager {

    private List<Expense> expenses;
    private FileHandler fileHandler;

    public ExpenseManager(String filePath) {
        this.expenses = new ArrayList<>();
        this.fileHandler = new FileHandler(filePath);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public void deleteExpense(int expenseId) {
        expenses.removeIf(e -> e.getId() == expenseId);
    }

    public List<Expense> getAllExpenses() {
        return expenses;
    }

    public void saveData() {
        fileHandler.saveExpensesToFile(expenses);
    }

    public void loadData() {
        expenses = fileHandler.readExpensesFromFile();
    }

}
