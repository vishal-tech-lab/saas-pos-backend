package com.example.Backend.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Entity.User;
import com.example.Backend.Service.AdminService;

@RestController
@RequestMapping("/admin")
public class Admincontroller {

    @Autowired
    private AdminService service;

    // ✅ Get ALL users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    // ✅ Get Pending Users
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(service.getPendingUsers());
    }

    // ✅ Approve User
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        service.approveUser(id);
        return ResponseEntity.ok(Map.of("message", "User Approved"));
    }

    // ✅ Reject User
    @PutMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        service.rejectUser(id);
        return ResponseEntity.ok(Map.of("message", "User Rejected"));
    }

    // ✅ Make Admin
    @PutMapping("/make-admin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable Long id) {
        service.makeAdmin(id);
        return ResponseEntity.ok(Map.of("message", "Promoted to Admin"));
    }
}