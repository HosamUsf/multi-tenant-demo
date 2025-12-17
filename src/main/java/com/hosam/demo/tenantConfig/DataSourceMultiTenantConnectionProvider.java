package com.hosam.demo.tenantConfig;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hibernate multi-tenant connection provider that supplies DataSource connections
 * based on the current tenant identifier.
 *
 * This class extends Hibernate's AbstractDataSourceBasedMultiTenantConnectionProviderImpl
 * to implement the "database per tenant" multi-tenancy strategy. Each tenant has its own
 * separate database, and this provider dynamically routes connections to the appropriate
 * tenant database based on the tenant identifier.
 *
 * Key features:
 * - Caches tenant DataSources in a ConcurrentHashMap for performance
 * - Lazily creates DataSource connections on first access
 * - Looks up tenant database credentials from the TenantInfo repository
 * - Falls back to default DataSource when no tenant is specified
 *
 * @author HosamUsf
 */
@Component
public class DataSourceMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> {

    private static final long serialVersionUID = 1L;

    private final transient DataSource defaultDataSource;

    /** Repository to fetch tenant database connection details */
    private final TenantInfoRepository tenantRepo;

    /** Cache of tenant DataSources to avoid recreating connections */
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public DataSourceMultiTenantConnectionProvider(
            @Qualifier("defaultDataSource") DataSource defaultDataSource,
            TenantInfoRepository tenantRepo) {
        this.defaultDataSource = defaultDataSource;
        this.tenantRepo = tenantRepo;
    }

    /**
     * Returns the default DataSource when Hibernate needs any connection
     * (e.g., during startup or when no tenant context is set).
     *
     * @return The default/master DataSource
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return defaultDataSource;
    }

    /**
     * Selects the appropriate DataSource for the given tenant identifier.
     * Uses memoization to cache and reuse DataSource instances.
     *
     * @param tenantIdentifier The tenant ID to get the DataSource for
     * @return DataSource configured for the specified tenant
     */
    protected DataSource selectDataSource(Object tenantIdentifier) {
        String tenantId = tenantIdentifier.toString();
        // Use computeIfAbsent to lazily create and cache DataSources
        return dataSources.computeIfAbsent(tenantId, this::createDataSource);
    }

    /**
     * Creates a new DataSource for the given tenant by fetching
     * connection details from the TenantInfo repository.
     *
     * @param tenantId The tenant ID to create a DataSource for
     * @return Newly created DataSource for the tenant
     * @throws IllegalArgumentException if tenant is not found or inactive
     */
    private DataSource createDataSource(String tenantId) {
        TenantInfo info = tenantRepo.findByTenantIdAndActiveIsTrue(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant " + tenantId));

        return DataSourceBuilder.create()
                .driverClassName(info.getDbDriver())
                .url(info.getDbUrl())
                .username(info.getDbUsername())
                .password(info.getDbPassword())
                .build();
    }

}
