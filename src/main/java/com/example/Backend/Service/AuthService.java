package com.example.Backend.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Tenant;
import com.example.Backend.Entity.User;
import com.example.Backend.Repository.TenantRepository;

@Service
public class AuthService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder encoder;

    public User login(String username, String password) {

        Tenant tenant = tenantRepository.findByUsername(username);

        if (tenant == null) throw new RuntimeException("Tenant not found");

        String schemaName = tenant.getSchemaName();

        User user = null;

        try (Connection connection = dataSource.getConnection()) {

            connection.setSchema(schemaName);

            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username=?");

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                user = new User();

                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setSchema(schemaName);
            }

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }

        if (user == null) throw new RuntimeException("User not found");

        if (!encoder.matches(password, user.getPassword())) throw new RuntimeException("Invalid password");

        if (!"APPROVED".equals(user.getStatus())) throw new RuntimeException("Your account is not approved yet");

        return user;
    }
}