package com.example.Backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.User;
import com.example.Backend.Repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository repository;

    // ✅ All users
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    // ✅ Pending users
    public List<User> getPendingUsers() {
        return repository.findByStatus("PENDING");
    }

    // ✅ Approve
    public void approveUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus("APPROVED");
        repository.save(user);
    }

    // ✅ Reject
    public void rejectUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus("REJECTED");
        repository.save(user);
    }

    // ✅ Make Admin
    public void makeAdmin(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole("ADMIN");
        repository.save(user);
    }
}