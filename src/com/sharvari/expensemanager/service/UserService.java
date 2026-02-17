package com.sharvari.expensemanager.service;

import com.sharvari.expensemanager.model.User;
import com.sharvari.expensemanager.repository.UserRepository;

import java.util.Optional;


public class UserService {

    private final UserRepository userRepo;

    public UserService() {
        this.userRepo = new UserRepository();
    }

    public User register(String username, String password, String email, String currency) {
        if (userRepo.usernameExists(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }
        User user = new User(0, username, password, email, currency);
        userRepo.addUser(user);
        System.out.println("✅ Registered successfully! Your user ID: " + user.getUserId());
        return user;
    }

    public Optional<User> login(String username, String password) {
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
