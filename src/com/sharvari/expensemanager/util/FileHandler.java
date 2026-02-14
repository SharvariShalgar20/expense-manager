package com.sharvari.expensemanager.util;

import com.sharvari.expensemanager.model.User;
import com.sharvari.expensemanager.model.Expense;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }


    public void saveUsersToFile(List<User> users){

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (User user : users) {

                // Write User details
                writer.write("USER," + user.getUserId() + "," +
                        user.getName() + "," +
                        user.getEmail());
                writer.newLine();

                // Write Expenses of that user
                for (Expense expense : user.getExpenses()) {
                    writer.write("EXPENSE," +
                            expense.getId() + "," +
                            expense.getCategory() + "," +
                            expense.getAmount());
                    writer.newLine();
                }

                writer.write("END_USER");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<User> readUsersFromFile() {
        return new ArrayList<>(); // need to implement
    }
}
