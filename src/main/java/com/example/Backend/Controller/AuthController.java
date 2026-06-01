package com.example.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Dto.LoginRequest;
import com.example.Backend.Dto.LoginResponse;
import com.example.Backend.Entity.User;
import com.example.Backend.Service.AuthService;
import com.example.Backend.Security.JwtUtil;
import com.example.Backend.multitenancy.tenant.TenantContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.core.env.Environment;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private Environment env;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh.expiration}")
    private long jwtRefreshExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        try {
            User user = service.login(req.getUsername(), req.getPassword());

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            boolean isSecure = !"dev".equals(env.getProperty("spring.profiles.active", "dev"));

            ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite("Strict")
                .maxAge(jwtExpirationMs / 1000)
                .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .sameSite("Strict")
                .maxAge(jwtRefreshExpirationMs / 1000)
                .build();

            LoginResponse response = new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getStatus(),
                    TenantContext.getTenant(),
                    user.getBranch() != null ? user.getBranch().getBranchid() : null,
                    user.getBranch() != null ? user.getBranch().getBranchname() : null,
                    user.getBranch() != null ? user.getBranch().getBranchtype() : null
            );

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
        } catch (RuntimeException e) {
            logger.warn("Failed login attempt: {}", req.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(
                            java.util.Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            logger.warn("Refresh token missing");
            return ResponseEntity.status(401).body(Map.of("message", "Refresh token missing"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            logger.warn("Invalid refresh token");
            return ResponseEntity.status(401).body(Map.of("message", "Invalid refresh token"));
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = service.findByUsername(username);
        if (user == null) {
            logger.warn("Refresh token user not found: {}", username);
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }

        String accessToken = jwtUtil.generateAccessToken(user);

        boolean isSecure = !"dev".equals(env.getProperty("spring.profiles.active", "dev"));

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
            .httpOnly(true)
            .secure(isSecure)
            .path("/")
            .sameSite("Strict")
            .maxAge(jwtExpirationMs / 1000)
            .build();

        LoginResponse response = new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getStatus(),
            TenantContext.getTenant(),
            user.getBranch() != null ? user.getBranch().getBranchid() : null,
            user.getBranch() != null ? user.getBranch().getBranchname() : null,
            user.getBranch() != null ? user.getBranch().getBranchtype() : null
        );

        logger.info("Refresh token accepted for user: {}", username);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .body(response);
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout() {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .sameSite("Strict")
            .build();

        logger.info("Logout requested");
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(Map.of("message", "Logged out"));
        }
        @PostMapping("/signup")
public ResponseEntity<?> signup(
        @RequestBody LoginRequest req
) {

    try {

        service.createAdmin(
                req.getUsername(),
                req.getPassword()
        );

        logger.info("Signup success: {}", req.getUsername());
        return ResponseEntity.ok(
            Map.of(
                "message",
                "Admin created successfully"
            )
        );

    } catch (Exception e) {

        logger.error("Signup failed for user: {}", req.getUsername(), e);
        return ResponseEntity.badRequest()
            .body(
                Map.of(
                    "message",
                    e.getMessage()
                )
            );
    }
}
}
