package com.hosam.demo.tenantConfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate configuration for multi-tenancy support.
 *
 * This configuration sets up the primary EntityManagerFactory with multi-tenant capabilities.
 * It configures Hibernate to use:
 * - DataSourceMultiTenantConnectionProvider: Routes connections to tenant-specific databases
 * - TenantIdentifierResolver: Determines the current tenant from the request context
 * @author HosamUsf
 */
@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor

public class HibernateConfig {

    // The default DataSource for initial Hibernate setup
    private final DataSource dataSource;

    private final DataSourceMultiTenantConnectionProvider multiTenantConnectionProvider;

    private final TenantIdentifierResolver currentTenantIdentifierResolver;

    /**
     * Creates the primary EntityManagerFactory configured for multi-tenancy.
     *
     * This factory scans "com.hosam.demo" for entity classes and configures
     * Hibernate with multi-tenant connection routing.
     *
     * @return Configured EntityManagerFactory bean
     */
    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.hosam.demo");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        // Multi-tenancy configuration
        properties.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        properties.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        // SQL logging and debugging
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.FORMAT_SQL, "true");
        properties.put(Environment.GENERATE_STATISTICS, "true");
        properties.put(Environment.LOG_JDBC_WARNINGS, "true");

        // Batch processing optimization
        properties.put(Environment.STATEMENT_BATCH_SIZE, "30");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");

        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * Creates the primary transaction manager for JPA operations.
     *
     * @param emf The EntityManagerFactory to manage transactions for
     * @return Configured JpaTransactionManager
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }
}