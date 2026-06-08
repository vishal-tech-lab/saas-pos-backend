package com.example.Backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Backend.Entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsBySchemaName(String schemaName);

    boolean existsBySubdomain(String subdomain);

    Tenant findByUsername(String username);
    Optional<Tenant> findBySubdomain(
        String subdomain
);
}