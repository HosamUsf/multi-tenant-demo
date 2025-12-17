package com.hosam.demo.tenantConfig;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Configuration class for setting up the default (master) DataSource.
 *
 * This DataSource connects to the master/default database which stores
 * tenant metadata information (tenant_info table). It is used by Hibernate
 * to look up tenant connection details before routing to tenant-specific databases.
 *
 * The configuration is read from application.properties using the "spring.datasource.*" prefix.
 *
 * @author HosamUsf
 */
@Configuration
public class DataSourceConfig {

    /**
     * Creates DataSourceProperties bean populated from "spring.datasource.*" properties.
     * This includes URL, username, password, and driver class name for the master database.
     *
     * @return DataSourceProperties containing database connection configuration
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Creates the default/master DataSource bean using the configured properties.
     * This DataSource is marked as @Primary so it will be injected by default
     * when no specific qualifier is specified.
     *
     * Used for:
     * - Reading tenant metadata from the tenant_info table
     * - Fallback when no tenant is specified
     *
     * @param properties The DataSourceProperties containing connection configuration
     * @return DataSource configured for the master database
     */
    @Bean(name = "defaultDataSource")
    @Primary
    public DataSource defaultDataSource(DataSourceProperties properties) {
        // this will set jdbcUrl, driverClassName, username, password, etc.
        return properties
                .initializeDataSourceBuilder()
                .build();
    }
}
