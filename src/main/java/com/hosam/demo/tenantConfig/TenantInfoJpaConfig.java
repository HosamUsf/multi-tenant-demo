package com.hosam.demo.tenantConfig;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * JPA configuration specifically for the TenantInfo entity and repository.
 *
 * This configuration creates a separate EntityManagerFactory and TransactionManager
 * for accessing the master/default database where tenant metadata is stored.
 *
 * Why a separate configuration?
 * - The main HibernateConfig uses multi-tenant connection routing
 * - TenantInfo must always be read from the master database
 * - This prevents circular dependency issues during tenant resolution
 *
 * The @EnableJpaRepositories annotation configures Spring Data JPA to use:
 * - tenantInfoEmf: For entity management (reads TenantInfo from master DB)
 * - tenantInfoTx: For transaction management on the master DB
 *
 * @author HosamUsf
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.hosam.demo.tenantConfig", // TenantInfo repository package
        entityManagerFactoryRef = "tenantInfoEmf",
        transactionManagerRef = "tenantInfoTx"
)
@RequiredArgsConstructor
public class TenantInfoJpaConfig {

    /** The default/master DataSource for tenant metadata access */
    private final DataSource defaultDataSource;

    /**
     * Creates an EntityManagerFactory dedicated to the TenantInfo entity.
     *
     * This EMF:
     * - Uses the default/master DataSource
     * - Only scans the tenantConfig package for entities
     * - Is configured for MySQL 8 dialect
     * - Validates schema instead of creating/updating it
     *
     * @return LocalContainerEntityManagerFactoryBean for tenant metadata
     */
    @Bean(name = "tenantInfoEmf")
    public LocalContainerEntityManagerFactoryBean tenantInfoEmf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(defaultDataSource);
        emf.setPackagesToScan("com.hosam.demo.tenantConfig"); // TenantInfo entity package
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> jpaProps = new HashMap<>();
        // MySQL 8 dialect for proper SQL generation
        jpaProps.put("hibernate.dialect",
                "org.hibernate.dialect.MySQL8Dialect");
        // Validate schema - assumes tables already exist
        jpaProps.put("hibernate.hbm2ddl.auto", "validate");

        emf.setJpaPropertyMap(jpaProps);
        return emf;
    }

    /**
     * Creates a TransactionManager for TenantInfo operations.
     *
     * This ensures transactions on the master database are managed
     * separately from tenant database transactions.
     *
     * @param emf The tenantInfoEmf EntityManagerFactory
     * @return PlatformTransactionManager for tenant metadata operations
     */
    @Bean(name = "tenantInfoTx")
    public PlatformTransactionManager tenantInfoTx(
            @Qualifier("tenantInfoEmf") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
