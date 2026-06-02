package com.example.Backend.Security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.Backend.Entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh.expiration}")
    private long jwtRefreshExpirationMs;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

 public String generateAccessToken(
        User user,
        String tenant
) {

    return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("role", user.getRole())
            .claim("tenant", tenant)
            .setIssuedAt(new Date())
            .setExpiration(
                new Date(
                    System.currentTimeMillis()
                    + jwtExpirationMs
                )
            )
            .signWith(
                signingKey(),
                SignatureAlgorithm.HS256
            )
            .compact();
}

    public String generateRefreshToken(User user,        String tenant
) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .claim("tenant", tenant)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
    public String extractTenant(
        String token
) {

    Claims claims =
            Jwts.parserBuilder()
                    .setSigningKey(
                            signingKey()
                    )
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

    return claims.get(
            "tenant",
            String.class
    );
}
}
