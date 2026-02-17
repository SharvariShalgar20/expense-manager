package com.sharvari.expensemanager.repository;

import com.sharvari.expensemanager.model.User;
import com.sharvari.expensemanager.util.FileUtil;

import java.util.*;
import java.util.stream.Collectors;


public class UserRepository {

    private static final String USERS_FILE = "data/users.txt";
    private List<User> users = new ArrayList<>();

    public UserRepository() {
        FileUtil.ensureFileExists(USERS_FILE);
        load();
    }

    private void load() {
        users = FileUtil.readLines(USERS_FILE).stream()
                .map(User::fromFileString)
                .collect(Collectors.toList());
    }

    public void save() {
        List<String> lines = users.stream()
                .map(User::toFileString)
                .collect(Collectors.toList());
        FileUtil.writeLines(USERS_FILE, lines);
    }

    public void addUser(User user) {
        user.setUserId(nextId());
        users.add(user);
        save();
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<User> findById(int userId) {
        return users.stream()
                .filter(u -> u.getUserId() == userId)
                .findFirst();
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public List<User> findAll() { return Collections.unmodifiableList(users); }

    private int nextId() {
        return users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;
    }
}
