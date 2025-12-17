package com.hosam.demo.tenantConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that extracts the tenant identifier from incoming HTTP requests
 * and sets it in the TenantContext for the duration of the request.
 *
 * Tenant resolution priority:
 * 1. Already set in TenantContext (e.g., by JwtTenantValidationFilter)
 * 2. "X-Tenantid" HTTP header
 * 3. Falls back to "default" tenant
 *
 * This filter runs with @Order(2) to ensure it executes after authentication
 * filters that may set the tenant from JWT tokens.
 *
 * The filter ensures proper cleanup by always calling TenantContext.clear()
 * in the finally block, preventing tenant data from leaking between requests.
 *
 * @author HosamUsf
 */
@Component
@Order(2) // Ensure this runs after JwtTenantValidationFilter
public class TenantFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if tenant is already set by JwtTenantValidationFilter
        String tenant = TenantContext.getCurrentTenant();

        // If not set, check if it's in the header
        if (tenant == null) {
            tenant = request.getHeader("X-Tenantid");

            // If still not found, use default
            if (tenant == null) {
                tenant = "default";
                logger.debug("No tenant found in context or header, using default tenant");
            }

            // Set tenant in context
            TenantContext.setCurrentTenant(tenant);
        }

        logger.debug("Using tenant: {}", tenant);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clean up the context after the request to prevent memory leaks
            // and tenant data bleeding between requests in thread pools
            TenantContext.clear();
        }
    }
}