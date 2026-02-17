package com.sharvari.expensemanager;

import com.sharvari.expensemanager.model.*;
import com.sharvari.expensemanager.service.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static UserService userService = new UserService();
    static ExpenseService expenseService = new ExpenseService();
    static ReportService reportService = new ReportService(expenseService);
    static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   Multi-User Expense Manager ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // ─── Auth ──────────────────────────────────────────────────────────────────

    static void showAuthMenu() {
        System.out.println("\n1. Login\n2. Register\n3. Exit");
        System.out.print("Choose: ");
        int choice = readInt();
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 3 -> { System.out.println("Goodbye!"); System.exit(0); }
            default -> System.out.println("Invalid option.");
        }
    }

    static void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        Optional<User> user = userService.login(username, password);
        if (user.isPresent()) {
            currentUser = user.get();
            System.out.println("✅ Welcome back, " + currentUser.getUsername() + "!");
        } else {
            System.out.println("❌ Invalid credentials.");
        }
    }

    static void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Preferred Currency (e.g. INR, USD): ");
        String currency = scanner.nextLine().toUpperCase();
        try {
            currentUser = userService.register(username, password, email, currency);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    // ─── Main Menu ─────────────────────────────────────────────────────────────

    static void showMainMenu() {
        System.out.println("\n╔═════════════════════════════════╗");
        System.out.printf("║ Logged in as: %-19s║%n", currentUser.getUsername());
        System.out.println("╠═════════════════════════════════╣");
        System.out.println("║  1. Add Expense                 ║");
        System.out.println("║  2. View All Expenses           ║");
        System.out.println("║  3. Delete Expense              ║");
        System.out.println("║  4. Search Expenses             ║");
        System.out.println("║  5. Filter by Category          ║");
        System.out.println("║  6. Monthly Overview (Report)   ║");
        System.out.println("║  7. Yearly Summary              ║");
        System.out.println("║  8. Set / View Budget           ║");
        System.out.println("║  9. View Recurring Expenses     ║");
        System.out.println("║ 10. Top Expenses This Month     ║");
        System.out.println("║ 11. Daily Breakdown             ║");
        System.out.println("║ 12. Logout                      ║");
        System.out.println("╚═════════════════════════════════╝");
        System.out.print("Choose: ");

        int choice = readInt();
        switch (choice) {
            case 1  -> addExpense();
            case 2  -> viewAll();
            case 3  -> deleteExpense();
            case 4  -> searchExpenses();
            case 5  -> filterByCategory();
            case 6  -> monthlyOverview();
            case 7  -> yearlySummary();
            case 8  -> budgetMenu();
            case 9  -> reportService.printRecurringExpenses(currentUser.getUserId());
            case 10 -> topExpenses();
            case 11 -> dailyBreakdown();
            case 12 -> { currentUser = null; System.out.println("Logged out."); }
            default -> System.out.println("Invalid option.");
        }
    }

    // ─── Expense Actions ───────────────────────────────────────────────────────

    static void addExpense() {
        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Amount: ");
        double amount = readDouble();

        System.out.print("Category " + java.util.Arrays.toString(Category.values()) + ": ");
        Category category = Category.fromString(scanner.nextLine());

        System.out.print("Date (yyyy-MM-dd) [blank = today]: ");
        String dateStr = scanner.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Payment Mode " + java.util.Arrays.toString(PaymentMode.values()) + ": ");
        PaymentMode mode = PaymentMode.fromString(scanner.nextLine());

        System.out.print("Is this a recurring expense? (y/n): ");
        boolean recurring = scanner.nextLine().trim().equalsIgnoreCase("y");

        expenseService.addExpense(currentUser.getUserId(), title, amount, category,
                date, desc, mode, recurring);
    }

    static void viewAll() {
        List<Expense> all = expenseService.getAllExpenses(currentUser.getUserId());
        if (all.isEmpty()) { System.out.println("No expenses found."); return; }
        all.forEach(e -> System.out.println("  " + e));
        System.out.printf("%n  Total: %.2f %s%n",
                all.stream().mapToDouble(Expense::getAmount).sum(), currentUser.getCurrency());
    }

    static void deleteExpense() {
        viewAll();
        System.out.print("Enter Expense ID to delete: ");
        int id = readInt();
        expenseService.deleteExpense(currentUser.getUserId(), id);
    }

    static void searchExpenses() {
        System.out.print("Search keyword: ");
        String keyword = scanner.nextLine();
        List<Expense> results = expenseService.searchByKeyword(currentUser.getUserId(), keyword);
        if (results.isEmpty()) System.out.println("No matching expenses.");
        else results.forEach(e -> System.out.println("  " + e));
    }

    static void filterByCategory() {
        System.out.print("Category " + java.util.Arrays.toString(Category.values()) + ": ");
        Category cat = Category.fromString(scanner.nextLine());
        List<Expense> results = expenseService.getByCategory(currentUser.getUserId(), cat);
        if (results.isEmpty()) System.out.println("No expenses in this category.");
        else {
            results.forEach(e -> System.out.println("  " + e));
            System.out.printf("  Total: %.2f %s%n",
                    results.stream().mapToDouble(Expense::getAmount).sum(), currentUser.getCurrency());
        }
    }

    static void monthlyOverview() {
        YearMonth ym = askYearMonth();
        reportService.printMonthlyOverview(currentUser.getUserId(),
                ym.getMonthValue(), ym.getYear(), currentUser.getCurrency());
        reportService.printTopExpenses(currentUser.getUserId(),
                ym.getMonthValue(), ym.getYear(), 5);
        reportService.printDailyBreakdown(currentUser.getUserId(),
                ym.getMonthValue(), ym.getYear());
    }

    static void yearlySummary() {
        System.out.print("Year (e.g. 2025): ");
        int year = readInt();
        reportService.printYearlySummary(currentUser.getUserId(), year, currentUser.getCurrency());
    }

    static void budgetMenu() {
        System.out.println("1. Set Budget  2. View Budgets");
        int choice = readInt();
        if (choice == 1) {
            System.out.print("Category: ");
            Category cat = Category.fromString(scanner.nextLine());
            YearMonth ym = askYearMonth();
            System.out.print("Budget Limit (" + currentUser.getCurrency() + "): ");
            double limit = readDouble();
            expenseService.setBudget(currentUser.getUserId(), cat,
                    ym.getMonthValue(), ym.getYear(), limit);
        } else {
            expenseService.getRepo().findBudgetsByUser(currentUser.getUserId())
                    .forEach(b -> System.out.println("  " + b));
        }
    }

    static void topExpenses() {
        YearMonth ym = YearMonth.now();
        System.out.print("How many top expenses? [5]: ");
        String input = scanner.nextLine().trim();
        int n = input.isEmpty() ? 5 : Integer.parseInt(input);
        reportService.printTopExpenses(currentUser.getUserId(),
                ym.getMonthValue(), ym.getYear(), n);
    }

    static void dailyBreakdown() {
        YearMonth ym = askYearMonth();
        reportService.printDailyBreakdown(currentUser.getUserId(),
                ym.getMonthValue(), ym.getYear());
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    static YearMonth askYearMonth() {
        YearMonth now = YearMonth.now();
        System.out.printf("Month (MM/yyyy) [blank = %02d/%d]: ", now.getMonthValue(), now.getYear());
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return now;
        String[] parts = input.split("/");
        return YearMonth.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
    }

    static int readInt() {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount, defaulting to 0.");
            return 0;
        }
    }
}