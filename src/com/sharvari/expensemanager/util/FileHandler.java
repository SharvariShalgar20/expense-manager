package com.sharvari.expensemanager.util;

import com.sharvari.expensemanager.model.Category;
import com.sharvari.expensemanager.model.Expense;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }


    public void saveExpensesToFile(List<Expense> expenses) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Expense expense : expenses) {
                writer.write(
                        expense.getId() + "," +
                                expense.getTitle() + "," +
                                expense.getAmount() + "," +
                                expense.getCategory() + "," +
                                expense.getDate() + "," +
                                expense.getDescription()
                );
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<Expense> readExpensesFromFile() {

        List<Expense> expenses = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                Expense expense = new Expense(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Double.parseDouble(parts[2]),
                        Category.valueOf(parts[3]),
                        LocalDate.parse(parts[4]),
                        parts[5]
                );

                expenses.add(expense);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return expenses;
    }
}
