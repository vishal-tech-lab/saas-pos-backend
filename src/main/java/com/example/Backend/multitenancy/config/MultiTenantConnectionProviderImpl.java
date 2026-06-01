// ✅ MultiTenantConnectionProviderImpl.java

package com.example.Backend.multitenancy.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProviderImpl
        implements MultiTenantConnectionProvider<String> {

    private static final Logger logger =
            LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);

    private final DataSource dataSource;

    public MultiTenantConnectionProviderImpl(
            DataSource dataSource
    ) {

        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection()
            throws SQLException {

        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(
            Connection connection
    ) throws SQLException {

        connection.close();
    }

    @Override
    public Connection getConnection(
            String tenantIdentifier
    ) throws SQLException {

        logger.info("Switching database schema to tenant: {}", tenantIdentifier);

        Connection connection = getAnyConnection();

        try {
            connection.setSchema(tenantIdentifier);
        } catch (SQLException e) {
            logger.error("Database schema switch failed for tenant: {}", tenantIdentifier, e);
            throw e;
        }

        return connection;
    }

    @Override
    public void releaseConnection(
            String tenantIdentifier,
            Connection connection
    ) throws SQLException {

        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
