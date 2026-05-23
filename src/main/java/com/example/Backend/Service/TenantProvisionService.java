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

@Service
public class TenantProvisionService {

    @Autowired
    private DataSource dataSource;

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

            String encodedPassword = new BCryptPasswordEncoder().encode(request.getPassword());

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
}