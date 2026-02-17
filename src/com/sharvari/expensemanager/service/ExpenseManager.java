package com.sharvari.expensemanager.service;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.util.FileUtil;

import java.util.ArrayList;
import java.util.List;


public class ExpenseManager {

    private List<Expense> expenses;
    private FileUtil fileUtil;

    public ExpenseManager(String filePath) {
        this.expenses = new ArrayList<>();
        this.fileUtil = new FileUtil(filePath);
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
        fileUtil.saveExpensesToFile(expenses);
    }

    public void loadData() {
        expenses = fileUtil.readExpensesFromFile();
    }

}
