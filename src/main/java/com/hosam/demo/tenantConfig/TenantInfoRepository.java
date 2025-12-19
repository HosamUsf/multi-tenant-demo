package com.hosam.demo.tenantConfig;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TenantInfoRepository extends JpaRepository<TenantInfo, String> {

    Optional<TenantInfo> findByTenantId(String tenantId);

    @Query("SELECT t.tenantId FROM TenantInfo t")
    List<String> getAllTenantIds();
}
