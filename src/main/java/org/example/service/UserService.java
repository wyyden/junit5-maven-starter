package org.example.service;

import org.example.Dao.UserDao;
import org.example.dto.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public List<User> getAll() {
        return users;
    }

    public boolean add(User... users) {
        return this.users.addAll(Arrays.stream(users).collect(Collectors.toList()));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null){
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }
}
