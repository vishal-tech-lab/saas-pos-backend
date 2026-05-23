package com.example.Backend.Controller;

import java.util.HashMap;
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

HashMap<String, Object> response =
        new HashMap<>();

response.put("id", user.getId());

response.put("username", user.getUsername());

response.put("role", user.getRole());

response.put("status", user.getStatus());

response.put("schema", user.getSchema());

return ResponseEntity.ok(response);
        } catch (RuntimeException e) {

return ResponseEntity
        .badRequest()
        .body(
            Map.of(
                "message",
                e.getMessage()
            )
        );        }
    }
}