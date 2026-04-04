package com.Sharvari.expensemanager.service;
import com.Sharvari.expensemanager.model.Expense;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExpenseExportService {
    public void exportToCSV(int userId, List<Expense> expenses) {
        String path = "data/exports/user_" + userId + "_expenses_export.csv";
        try {
            Files.createDirectories(Paths.get(path).getParent());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                writer.write("ID,Title,Amount,Category,Date,Description,PaymentMode,Recurring");
                writer.newLine();
                for (Expense e : expenses) {
                    writer.write(String.format("%d,\"%s\",%.2f,%s,%s,\"%s\",%s,%s",
                            e.getExpenseId(),
                            e.getTitle(),
                            e.getAmount(),
                            e.getCategory(),
                            e.getDate(),
                            e.getDescription(),
                            e.getPaymentMode(),
                            e.isRecurring()
                    ));
                    writer.newLine();
                }
                System.out.println("✅ Exported to: " + path);
            }
        } catch (IOException ex) {
            System.err.println("❌ Export failed: " + ex.getMessage());
        }
    }
}
