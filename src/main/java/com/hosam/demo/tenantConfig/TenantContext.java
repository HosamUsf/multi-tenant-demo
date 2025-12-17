package com.hosam.demo.tenantConfig;

/**
 * Thread-local storage for the current tenant identifier.
 *
 * This class provides a static utility to store and retrieve the current
 * tenant ID within the scope of a single HTTP request thread. It uses
 * ThreadLocal to ensure tenant isolation between concurrent requests.
 *
 * Lifecycle:
 * 1. TenantFilter sets the tenant at the start of a request
 * 2. Various components read the tenant during request processing
 * 3. TenantFilter clears the tenant after the request completes
 *
 * Important: Always call clear() after request processing to prevent
 * memory leaks and tenant data bleeding between requests (especially
 * in thread pool scenarios).
 *
 * @author HosamUsf
 */
public class TenantContext {

    /** Thread-local storage for the current tenant identifier */
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Sets the current tenant identifier for this thread.
     *
     * @param tenantId The tenant identifier to set
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Gets the current tenant identifier for this thread.
     *
     * @return The current tenant ID, or null if not set
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Clears the current tenant identifier from this thread.
     *
     * Must be called after request processing to prevent memory leaks
     * and ensure clean thread reuse in thread pools.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
