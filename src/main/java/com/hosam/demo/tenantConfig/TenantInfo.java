package com.hosam.demo.tenantConfig;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


/**
 * Entity representing tenant database connection metadata.
 *
 * This entity is stored in the master/default database and contains
 * the connection details for each tenant's individual database.
 *
 * Table structure:
 * - tenant_id: Unique identifier for the tenant (e.g., "tenant1", "acme-corp")
 * - db_url: JDBC connection URL to the tenant's database
 * - db_username: Database username for authentication
 * - db_password: Database password for authentication
 * - db_driver: JDBC driver class name (e.g., "com.mysql.cj.jdbc.Driver")
 *
 * Used by DataSourceMultiTenantConnectionProvider to dynamically create
 * DataSource connections for each tenant.
 *
 * @author HosamUsf
 */
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tenant_info")
public class TenantInfo {

    /** Unique identifier for the tenant */
    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    /** JDBC URL to connect to the tenant's database */
    @Column(name = "db_url")
    private String dbUrl;

    /** Username for tenant database authentication */
    @Column(name = "db_username")
    private String dbUsername;

    /** Password for tenant database authentication */
    @Column(name = "db_password")
    private String dbPassword;

    /** JDBC driver class name for the tenant's database */
    @Column(name = "db_driver")
    private String dbDriver;
}
