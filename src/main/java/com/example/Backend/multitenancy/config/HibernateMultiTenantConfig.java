package com.example.Backend.multitenancy.config;

import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateMultiTenantConfig
        implements HibernatePropertiesCustomizer {

    @Autowired
    private MultiTenantConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private CurrentTenantIdentifierResolver tenantIdentifierResolver;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {

        

        hibernateProperties.put(
                AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER,
                multiTenantConnectionProvider
        );

        hibernateProperties.put(
                AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
                tenantIdentifierResolver
        );
    }
}