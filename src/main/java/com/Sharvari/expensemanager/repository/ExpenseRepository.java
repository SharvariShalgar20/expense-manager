package com.Sharvari.expensemanager.repository;

import com.Sharvari.expensemanager.db.DBConnection;
import com.Sharvari.expensemanager.model.*;

import java.sql.*;
import java.util.*;
import java.sql.Date;


public class ExpenseRepository {

    // ─── Expense CRUD ──────────────────────────────────────────────────────────

    public void addExpense(Expense expense) {
        String sql =
                    "Insert INTO expenses (user_id, title, amount, category, expense_date, description, payment_mode, is_recurring) VALUES (?, ?, ?, ?, ?, ?,? ,?)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, expense.getUserId());
            stmt.setString(2, expense.getTitle());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getCategory().name());
            stmt.setDate(5, Date.valueOf(expense.getDate()));
            stmt.setString(6, expense.getDescription());
            stmt.setString(7, expense.getPaymentMode().name());
            stmt.setBoolean(8, expense.isRecurring());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                // Note: Expense needs a setExpenseId() — add it to Expense.java

                int generatedId = keys.getInt(1);

                expense.setExpenseId(generatedId);
                System.out.println("✅ Expense added with ID: " + keys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding expense: " + e.getMessage());
        }
    }


    public List<Expense> findAllByUser (int userId) {
        List<Expense> result = new ArrayList<>();

        String sql = "SELECT * FROM expenses WHERE user_id = ? ORDER BY expense_date DESC";

        try ( PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) result.add(mapRow(rs));

        } catch (SQLException e) {
            System.err.println("❌ Error fetching expenses: " + e.getMessage());
        }
        return result;
    }

    private Expense mapRow(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("expense_id"),
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getDouble("amount"),
                Category.valueOf(rs.getString("category")),
                rs.getDate("expense_date").toLocalDate(),
                rs.getString("description"),
                PaymentMode.valueOf(rs.getString("payment_mode")),
                rs.getBoolean("is_recurring")
        );
    }

    public void deleteExpense(int userId, int expenseId) {

        String sql = "DELETE FROM expenses WHERE  expense_id = ? AND user_id = ?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, expenseId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch ( SQLException e ) {
            System.err.println("❌ Error deleting expense: " + e.getMessage());
        }
    }

    public void updateExpense(Expense e) {

        String sql = "UPDATE expenses SET title = ?, amount = ?, category = ?, expense_date = ?, description = ?, payment_mode = ?, is_recurring = ? WHERE expense_id = ? AND user_id = ?";

        try ( PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setString(1, e.getTitle());
            stmt.setDouble(2, e.getAmount());
            stmt.setString(3, e.getCategory().name());
            stmt.setDate(4, Date.valueOf(e.getDate()));
            stmt.setString(5, e.getDescription());
            stmt.setString(6, e.getPaymentMode().name());
            stmt.setBoolean(7, e.isRecurring());
            stmt.setInt(8, e.getExpenseId());
            stmt.setInt(9, e.getUserId());

            stmt.executeUpdate();

        } catch ( SQLException ex ){
            System.err.println("❌ Error : " + ex.getMessage());
        }
    }


    // ─── Budget CRUD ───────────────────────────────────────────────────────────
    public List<Budget> findBudgetsByUser(int userId) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT * FROM budgets WHERE user_id = ? ORDER BY year, month";

        try ( PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Budget(
                        rs.getInt("user_id"),
                        Category.valueOf(rs.getString("category")),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getDouble("limit_amount")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching budgets: " + e.getMessage());
        }
        return list;
    }

    public void saveBudget(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category, month, year, limit_amount) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE limit_amount = VALUES(limit_amount)";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt   (1, budget.getUserId());
            stmt.setString(2, budget.getCategory().name());
            stmt.setInt   (3, budget.getMonth());
            stmt.setInt   (4, budget.getYear());
            stmt.setDouble(5, budget.getLimit());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error saving budget: " + e.getMessage());
        }
    }

    public Optional<Budget> findBudget(int userId, Category category, int month, int year) {
        String sql = "SELECT * FROM budgets WHERE user_id=? AND category=? AND month=? AND year=?";

        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {

            stmt.setInt   (1, userId);
            stmt.setString(2, category.name());
            stmt.setInt   (3, month);
            stmt.setInt   (4, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Budget(
                        rs.getInt("user_id"),
                        Category.valueOf(rs.getString("category")),
                        rs.getInt("month"),
                        rs.getInt("year"),
                        rs.getDouble("limit_amount")
                ));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error finding budget: " + e.getMessage());
        }
        return Optional.empty();
    }

    public int nextExpenseId(int userId) {
        // DB generates IDs automatically now. Return 0 as placeholder.
        // The real ID comes back from RETURN_GENERATED_KEYS in addExpense().
        return 0;
    }

}
