package com.sharvari.expensemanager.model;

import java.util.Objects;

public class User {

    private int userId;
    private String username;
    private String password;
    private String email;
    private String currency; // e.g. INR, USD

    public User() {}

    public User(int userId, String username, String password, String email, String currency) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.currency = currency;
    }


    // Serialization: userId|username|password|email|currency
    public String toFileString() {
        return userId + "|" + username + "|" + password + "|" + email + "|" + currency;
    }

    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new User(
                Integer.parseInt(parts[0]),
                parts[1], parts[2], parts[3], parts[4]
        );
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getCurrency() { return currency; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', email='" + email + "', currency='" + currency + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() { return Objects.hash(userId); }



}
