package com.example.Backend.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.User;
import com.example.Backend.multitenancy.tenant.TenantContext;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private DataSource dataSource;


    @Autowired
    private PasswordEncoder encoder;

    public User login(String username, String password) {

        User user = null;

        try (Connection connection = dataSource.getConnection()) {

            String tenant = TenantContext.getTenant();
            connection.setSchema(tenant);
            logger.info("Tenant schema selected for login: {}", tenant);

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

            logger.error("Database exception during login for user: {}", username, e);
            throw new RuntimeException(e.getMessage());
        }

        if (user == null) {
            logger.warn("Failed login attempt, user not found: {}", username);
            throw new RuntimeException("User not found");
        }

        if (!encoder.matches(password, user.getPassword())) {
            logger.warn("Failed login attempt, invalid password: {}", username);
            throw new RuntimeException("Invalid password");
        }

        if (!"APPROVED".equals(user.getStatus())) {
            logger.warn("Failed login attempt, account not approved: {}", username);
            throw new RuntimeException("Your account is not approved yet");
        }

        logger.info("User login success: {}", username);
        return user;
    }

    public User findByUsername(String username) {

        User user = null;

        try (Connection connection = dataSource.getConnection()) {

            String tenant = TenantContext.getTenant();
            connection.setSchema(tenant);
            logger.info("Tenant schema selected for user lookup: {}", tenant);

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
            logger.error("Database exception during user lookup: {}", username, e);
            throw new RuntimeException(e.getMessage());
        }

        return user;
    }
    public void createAdmin(
        String username,
        String password
) {

    try (
        Connection connection =
            dataSource.getConnection()
    ) {

        connection.setSchema(
            TenantContext.getTenant()
        );
        logger.info("Tenant schema selected for admin creation: {}", TenantContext.getTenant());

        String hashedPassword =
            encoder.encode(password);

        PreparedStatement ps =
            connection.prepareStatement(

            "INSERT INTO users " +
            "(username,password,role,status) " +
            "VALUES (?,?,?,?)"

        );

        ps.setString(1, username);

        ps.setString(2, hashedPassword);

        ps.setString(
            3,
            "ROLE_ADMIN"
        );

        ps.setString(
            4,
            "APPROVED"
        );

        ps.executeUpdate();
        logger.info("Signup success, admin created: {}", username);

    } catch (Exception e) {

        logger.error("Database exception during admin signup: {}", username, e);
        throw new RuntimeException(
            e.getMessage()
        );
    }
}
}
