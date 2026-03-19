package com.Sharvari.expensemanager.repository;

import com.Sharvari.expensemanager.db.DBConnection;
import com.Sharvari.expensemanager.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class UserRepository {


    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, email, currency) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getCurrency());
            stmt.executeUpdate();

            // Get the auto-generated user_id from DB and set it on the object
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(keys.getInt(1));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding user: " + e.getMessage());
        }

    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user: " + e.getMessage());
        }

        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("currency")
        );
    }

    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error finding user by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching users: " + e.getMessage());
        }
        return Collections.unmodifiableList(users);
    }

}
