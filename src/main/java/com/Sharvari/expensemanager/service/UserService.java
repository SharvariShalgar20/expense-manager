package com.Sharvari.expensemanager.service;

import com.Sharvari.expensemanager.model.User;
import com.Sharvari.expensemanager.repository.UserRepository;

import java.util.Optional;


public class UserService {

    private final UserRepository userRepo;

    public UserService() {
        this.userRepo = new UserRepository();
    }

    public User register(String username, String password, String email, String currency) {

        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty!");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters!");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email address!");
        if (userRepo.usernameExists(username.trim()))
            throw new IllegalArgumentException("Username already exists!");


        User user = new User(0, username.trim(), password, email.trim(), currency.toUpperCase());
        userRepo.addUser(user);
        System.out.println("✅ Registered successfully! Your user ID: " + user.getUserId());
        return user;
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<User> found = userRepo.findByUsername(username);
        if (found.isPresent() && found.get().getPassword().equals(password)) {
            return found;
        }
        return Optional.empty();
    }

    public void listAllUsers() {
        userRepo.findAll().forEach(u ->
                System.out.println("  " + u.getUserId() + " | " + u.getUsername() + " | " + u.getEmail())
        );
    }
}
