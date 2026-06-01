package com.example.Backend.Service;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.Entity.Tenant;
import com.example.Backend.Repository.TenantRepository;
import com.example.Backend.Dto.TenantLoginResponse;
import com.example.Backend.Dto.TenantLoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
@Service
public class TenantProvisionService {

    @Autowired
    private DataSource dataSource;
@Autowired
private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantRepository tenantRepository;

    public String createTenant(TenantRequest request) {

        String companyName = request.getCompanyName();

        String schemaName = "tenant_" + companyName.toLowerCase().replace(" ", "_");

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {

            boolean exists = tenantRepository.existsBySchemaName(schemaName);

            if (exists) return "TENANT ALREADY EXISTS";

            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

            Flyway flyway = Flyway.configure().dataSource(dataSource).schemas(schemaName).locations("classpath:db/migration").load();

            flyway.migrate();

String encodedPassword =
        new BCryptPasswordEncoder()
                .encode(
                        request.getPassword()
                );

tenant.setPassword(
        encodedPassword
);
            statement.execute("INSERT INTO " + schemaName + ".users (username,password,role,status) VALUES ('" + request.getUsername() + "','" + encodedPassword + "','ADMIN','APPROVED')");

            Tenant tenant = new Tenant();

            tenant.setCompanyName(request.getCompanyName());

            tenant.setSchemaName(schemaName);

            tenant.setUsername(request.getUsername());

            tenantRepository.save(tenant);

            return "TENANT CREATED SUCCESSFULLY : " + schemaName;

        } catch (Exception e) {

            e.printStackTrace();

            return "ERROR : " + e.getMessage();
        }
    }
    public TenantLoginResponse login(
        String username,
        String password
) {

    Tenant tenant =
            tenantRepository
                    .findByUsername(username)
                    .orElseThrow(
                            () ->
                            new RuntimeException(
                                    "Tenant not found"
                            )
                    );

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
            "TENANT_OWNER"
    );
}
}