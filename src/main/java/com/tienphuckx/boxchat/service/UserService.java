package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.mapper.UserMapper;
import com.tienphuckx.boxchat.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User registerUser(User user) {
        // Check if username already exists
        User existingUser = userMapper.findUserByUsername(user.getUsername());
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Generate a unique user code
        String userCode = "USER" + System.currentTimeMillis();

        // Set user code and save user
        user.setUserCode(userCode);
        userMapper.insertUser(user);

        return user; // Return the saved user object
    }

    // Find a user by ID
    public User findUserById(Integer id) {
        return userMapper.findUserById(id);
    }

    // Find a user by username
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }
}
