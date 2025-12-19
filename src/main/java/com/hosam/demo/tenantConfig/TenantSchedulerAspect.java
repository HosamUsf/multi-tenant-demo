package com.hosam.demo.tenantConfig;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class TenantSchedulerAspect {

    private final TenantInfoRepository tenantInfoRepository;

    public TenantSchedulerAspect(TenantInfoRepository tenantInfoRepository) {
        this.tenantInfoRepository = tenantInfoRepository;
    }

    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object setTenantContext(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> tenantIds = tenantInfoRepository.getAllTenantIds();

        for (String tenantId : tenantIds) {
            try {
                TenantContext.setCurrentTenant(tenantId);
                joinPoint.proceed();
            } finally {
                TenantContext.clear();
            }
        }
        return null;
    }
}
