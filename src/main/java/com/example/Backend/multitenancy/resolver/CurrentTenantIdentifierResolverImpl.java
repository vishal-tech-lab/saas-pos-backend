
package com.example.Backend.multitenancy.resolver;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.Backend.multitenancy.tenant.TenantContext;

@Component
public class CurrentTenantIdentifierResolverImpl
        implements CurrentTenantIdentifierResolver<String> {

    private static final Logger logger =
            LoggerFactory.getLogger(CurrentTenantIdentifierResolverImpl.class);

    @Override
    public String resolveCurrentTenantIdentifier() {

        String tenant = TenantContext.getTenant();

        // During startup or background initialization there is no request tenant.
        // Default to the public schema for JPA bootstrapping. Runtime request
        // validation happens in TenantFilter.
        if (tenant == null || tenant.isBlank()) {
            logger.info("Resolved tenant: public");
            return "public";
        }

        logger.info("Resolved tenant: {}", tenant);
        return tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
