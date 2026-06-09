package com.example.Backend.Service;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Backend.Dto.TenantLoginResponse;
import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.Entity.Tenant;
import com.example.Backend.Repository.TenantRepository;

@Service
public class TenantProvisionService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createTenant(
            TenantRequest request
    ) {

        String companyName =
                request.getCompanyName();

        String subdomain =
                request.getSubdomain();

       if (subdomain == null || subdomain.isBlank()) {

    subdomain = companyName
            .toLowerCase()
            .trim()
            .replace(" ", "-")
            .replace("_", "-");

} else {

    subdomain = subdomain
            .toLowerCase()
            .trim()
            .replace(" ", "-")
            .replace("_", "-");
}

     String schemaName =
        "tenant_" +
        subdomain.replace("-", "_");

        try (
                Connection connection =
                        dataSource.getConnection();

                Statement statement =
                        connection.createStatement()
        ) {

            boolean exists =
                    tenantRepository
                            .existsBySchemaName(
                                    schemaName
                            );

            if (exists) {

                return "TENANT ALREADY EXISTS";
            }

            boolean subdomainExists =
                    tenantRepository
                            .existsBySubdomain(
                                    subdomain
                            );

            if (subdomainExists) {
                return "TENANT SUBDOMAIN ALREADY EXISTS";
            }

            statement.execute(
                    "CREATE SCHEMA IF NOT EXISTS "
                            + schemaName
            );

            Flyway flyway =
                    Flyway.configure()
                            .dataSource(dataSource)
                            .schemas(schemaName)
                            .locations("classpath:db/migration")
                            .load();

            flyway.migrate();

           String encodedPassword =
        passwordEncoder.encode(
                request.getPassword()
        );

            statement.execute(
                    "INSERT INTO "
                            + schemaName
                            + ".users (username,password,role,status) VALUES ('"
                            + request.getUsername()
                            + "','"
                            + encodedPassword
                            + "','ROLE_ADMIN','APPROVED')"
            );

            Tenant tenant =
                    new Tenant();

            tenant.setCompanyName(
                    request.getCompanyName()
            );

            tenant.setSchemaName(
                    schemaName
            );

            tenant.setUsername(
                    request.getUsername()
            );

            tenant.setPassword(
                    encodedPassword
            );

            tenant.setSubdomain(
                    subdomain
            );

           tenant.setPlan(
        request.getPlan() == null ||
        request.getPlan().isBlank()
                ? "BASIC"
                : request.getPlan().toUpperCase()
);

            tenant.setSubscriptionStartDate(
                    LocalDate.now()
            );

            tenant.setSubscriptionEndDate(
                    LocalDate.now().plusDays(30)
            );

            tenant.setSubscriptionStatus(
                    "ACTIVE"
            );

            tenantRepository.save(
                    tenant
            );

            return "TENANT CREATED SUCCESSFULLY : "
                    + schemaName;

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR : "
                    + e.getMessage();
        }
    }

    public TenantLoginResponse login(
            String username,
            String password
    ) {

        Tenant tenant =
                tenantRepository.findByUsername(
                        username
                );

        if (tenant == null) {

            throw new RuntimeException(
                    "Tenant not found"
            );
        }

        if (
                tenant.getSubscriptionEndDate() != null
                &&
                tenant.getSubscriptionEndDate()
                        .isBefore(LocalDate.now())
        ) {

            tenant.setSubscriptionStatus(
                    "EXPIRED"
            );

            tenantRepository.save(
                    tenant
            );

            throw new RuntimeException(
                    "Subscription Expired"
            );
        }

        if (
                !passwordEncoder.matches(
                        password,
                        tenant.getPassword()
                )
        ) {

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        return new TenantLoginResponse(
        tenant.getSchemaName(),
        tenant.getUsername(),
        "TENANT_OWNER",
        tenant.getPlan(),
        tenant.getSubscriptionStatus()
);
    }
}