package com.example.Backend.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Backend.multitenancy.tenant.TenantContext;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try (Connection connection = dataSource.getConnection()) {

            String tenant = TenantContext.getTenant();
            if (tenant == null || tenant.isBlank()) {
                logger.error("Tenant context is null for user details lookup of username: {}", username);
                throw new UsernameNotFoundException("Tenant context missing");
            }

            connection.setSchema(tenant);

            logger.info("Loading user details - username: {}, tenant: {}, schema: {}", username, tenant, connection.getSchema());

            PreparedStatement ps = connection.prepareStatement(
                "SELECT u.*, b.branchid AS bid, b.branchname AS bname, b.branchtype AS btype " +
                "FROM users u " +
                "LEFT JOIN branches b ON u.branchid = b.branchid " +
                "WHERE u.username = ?"
            ); 

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                logger.warn("User details lookup failed, user not found: {}", username);
                throw new UsernameNotFoundException("User not found");
            }

            String password = rs.getString("password");
            String role = rs.getString("role");
            String status = rs.getString("status");

            boolean enabled = "APPROVED".equals(status);

            List<GrantedAuthority> authorities = new ArrayList<>();
            if (role != null) authorities.add(new SimpleGrantedAuthority(role));

            logger.info("User details loaded: {}", username);
            return new org.springframework.security.core.userdetails.User(username, password, enabled, true, true, true, authorities);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Database exception during user details lookup: {}", username, e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
