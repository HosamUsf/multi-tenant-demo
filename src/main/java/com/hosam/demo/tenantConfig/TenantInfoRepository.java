package com.hosam.demo.tenantConfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing tenant metadata from the master database.
 *
 * This repository operates on the master/default database (not tenant databases)
 * and is used to look up tenant connection details when routing requests.
 *
 * Configured in TenantInfoJpaConfig to use the tenantInfoEmf EntityManagerFactory
 * which is separate from the multi-tenant EntityManagerFactory.
 *
 * @author HosamUsf
 */
public interface TenantInfoRepository extends JpaRepository<TenantInfo, String> {

    /**
     * Finds an active tenant by its identifier.
     *
     * Only returns tenants that are marked as active, preventing
     * connections to disabled or suspended tenant databases.
     *
     * @param tenantId The tenant identifier to look up
     * @return Optional containing the TenantInfo if found and active
     */
    Optional<TenantInfo> findByTenantIdAndActiveIsTrue(String tenantId);
}
