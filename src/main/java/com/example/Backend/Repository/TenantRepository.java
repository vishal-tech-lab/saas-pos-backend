package com.example.Backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Backend.Entity.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsBySchemaName(String schemaName);

    Tenant findByUsername(String username);
}