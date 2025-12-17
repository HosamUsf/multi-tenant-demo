package com.hosam.demo.tenantConfig;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Hibernate tenant identifier resolver that retrieves the current tenant
 * from the TenantContext thread-local storage.
 *
 * This class implements Hibernate's CurrentTenantIdentifierResolver interface,
 * allowing Hibernate to determine which tenant's database to use for each
 * database operation.
 *
 * The resolver is called by Hibernate whenever it needs to:
 * - Open a new database connection
 * - Validate the current session's tenant
 * - Route queries to the correct tenant database
 *
 * If no tenant is set in the context, it falls back to "default" tenant.
 *
 * @author HosamUsf
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    private static final Logger logger = LoggerFactory.getLogger(TenantIdentifierResolver.class);

    /** Fallback tenant identifier when none is set in context */
    private static final String DEFAULT_TENANT = "default";

    /**
     * Resolves the current tenant identifier from TenantContext.
     * Called by Hibernate to determine which tenant's database
     * should be used for the current operation.
     *
     * @return The current tenant ID, or "default" if none is set
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant == null) {
            logger.debug("No tenant found in context, using default tenant: {}", DEFAULT_TENANT);
            return DEFAULT_TENANT;
        }
        logger.debug("Resolved tenant identifier: {}", currentTenant);
        return currentTenant;
    }

    /**
     * Indicates whether existing sessions should be validated against
     * the current tenant identifier.
     *
     * Returning true means Hibernate will validate that existing sessions
     * match the current tenant, providing additional safety against
     * cross-tenant data access.
     *
     * @return true to enable session validation
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}