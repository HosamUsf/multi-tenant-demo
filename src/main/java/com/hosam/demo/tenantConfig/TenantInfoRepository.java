package com.hosam.demo.tenantConfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TenantInfoRepository extends JpaRepository<TenantInfo, String> {

    Optional<TenantInfo> findByTenantId(String tenantId);
}
