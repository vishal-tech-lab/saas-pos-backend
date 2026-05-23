package com.example.Backend.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Dto.LoginRequest;
import com.example.Backend.Entity.User;
import com.example.Backend.Service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        try {

            User user = service.login(req.getUsername(), req.getPassword());

            return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername(), "role", user.getRole(), "status", user.getStatus(), "schema", user.getSchema()));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}