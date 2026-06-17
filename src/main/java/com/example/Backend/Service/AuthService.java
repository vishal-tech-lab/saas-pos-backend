package com.example.Backend.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.Tenant;
import com.example.Backend.Entity.User;
import com.example.Backend.Repository.TenantRepository;
import com.example.Backend.Repository.UserRepository;
import com.example.Backend.multitenancy.tenant.TenantContext;

@Service
public class AuthService {

    @Autowired
    private DataSource dataSource;
@Autowired
private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    public User login(String username, String password) {

        User user = null;

        try (Connection connection = dataSource.getConnection()) {

            String tenant = TenantContext.getTenant();
            connection.setSchema(tenant);

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT u.*, b.branchid AS bid, b.branchname AS bname, b.branchtype AS btype " +
                            "FROM users u LEFT JOIN branches b ON u.branchid = b.branchid WHERE u.username = ?"
            );

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                user = new User();

                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));

                Long branchId = rs.getLong("bid");
                if (!rs.wasNull()) {
                    Branch branch = new Branch();
                    branch.setBranchid(branchId);
                    branch.setBranchname(rs.getString("bname"));
                    branch.setBranchtype(rs.getString("btype"));
                    user.setBranch(branch);
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }

        if (user == null)
    throw new RuntimeException("Invalid username or password");

if (!encoder.matches(password, user.getPassword()))
    throw new RuntimeException("Invalid username or password");

if (!"APPROVED".equals(user.getStatus()))
    throw new RuntimeException("Invalid username or password");

        return user;
    }

    public User findByUsername(String username) {

        User user = null;

        try (Connection connection = dataSource.getConnection()) {

            String tenant = TenantContext.getTenant();
            connection.setSchema(tenant);

            PreparedStatement ps = connection.prepareStatement(
                    "SELECT u.*, b.branchid AS bid, b.branchname AS bname, b.branchtype AS btype " +
                            "FROM users u LEFT JOIN branches b ON u.branchid = b.branchid WHERE u.username = ?"
            );

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                user = new User();

                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));

                Long branchId = rs.getLong("bid");
                if (!rs.wasNull()) {
                    Branch branch = new Branch();
                    branch.setBranchid(branchId);
                    branch.setBranchname(rs.getString("bname"));
                    branch.setBranchtype(rs.getString("btype"));
                    user.setBranch(branch);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return user;
    }
    public void createAdmin(
        String username,
        String password
) {

    User existingUser =
            userRepository.findByUsername(
                    username
            );

    if (existingUser != null) {

        throw new RuntimeException(
                "Username already exists"
        );
    }

    User user =
            new User();

    user.setUsername(
            username
    );

    user.setPassword(
            encoder.encode(
                    password
            )
    );

    user.setRole(
            "ROLE_ADMIN"
    );

    user.setStatus(
            "APPROVED"
    );

    userRepository.save(
            user
    );
}
}