package com.example.tradingjournal.service;

import com.example.tradingjournal.model.User;
import com.example.tradingjournal.model.enums.Role;
import com.example.tradingjournal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found witd id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User delegateAdmin(Long id) {
        User existing = getUserById(id);
        existing.setRole(Role.ADMIN);
        return userRepository.save(existing);
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(existing ->{
                    existing.setEmail(updatedUser.getEmail());
                    existing.setPassword(updatedUser.getPassword());
                    existing.setUsername(updatedUser.getUsername());
                    return userRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));


    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
