package com.sharvari.expensemanager;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.service.ExpenseManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    static void main(String[] args) {

        ExpenseManager manager = new ExpenseManager("data/expenses.txt");

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== Expense Manager =====");
            System.out.println("1. Add Expense");
            System.out.println("2. Save Data");
            System.out.println("3. View All Expenses");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // clear buffer

            switch (choice) {

                case 1:

                    System.out.print("Enter Expense ID: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter Amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Enter Category (FOOD, TRAVEL, SHOPPING, BILLS, ENTERTAINMENT, OTHER): ");
                    String categoryInput = scanner.nextLine();
                    Category category = Category.valueOf(categoryInput.toUpperCase());

                    System.out.print("Enter Date (yyyy-mm-dd): ");
                    LocalDate date = LocalDate.parse(scanner.nextLine());

                    System.out.print("Enter Description: ");
                    String description = scanner.nextLine();

                    Expense expense = new Expense(id, title, amount, category, date, description);

                    manager.addExpense(expense);

                    System.out.println("Expense Added Successfully!");
                    break;


                case 2:
                    manager.saveData();
                    System.out.println("Data Saved Successfully!");
                    break;


                case 3:
                    for (Expense e : manager.getAllExpenses()) {
                        System.out.println(e);
                    }
                    break;


                case 4:
                    System.out.println("Exiting...");
                    return;


                default:
                    System.out.println("Invalid Option!");
            }
        }


    }
}
