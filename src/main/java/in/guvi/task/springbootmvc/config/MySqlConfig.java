package in.guvi.task.springbootmvc.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Database configuration class for the MySQL data source.
 *
 * <p>This class manually wires up the full JPA stack — DataSource, EntityManagerFactory,
 * and TransactionManager — using properties defined under the {@code app.datasource.mysql.*}
 * prefix in {@code application.properties}. This explicit approach is used instead of
 * Spring Boot's auto-configuration, giving fine-grained control over the persistence unit.
 *
 * <ul>
 *   <li>{@code @Configuration}            - marks this as a Spring configuration class (bean factory)</li>
 *   <li>{@code @EnableTransactionManagement} - activates annotation-driven transaction management (@Transactional)</li>
 *   <li>{@code @EnableJpaRepositories}    - tells Spring Data JPA which package to scan for repository interfaces,
 *                                           and which EntityManagerFactory / TransactionManager beans to use</li>
 * </ul>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "in.guvi.task.springbootmvc.repository",   // Only scan this package for JPA repos
        entityManagerFactoryRef = "mysqlEntityManagerFactory",     // Wire repos to our custom EM factory
        transactionManagerRef = "mysqlTransactionManager"          // Wire repos to our custom TX manager
)
public class MySqlConfig {

    /**
     * Reads MySQL connection properties (url, username, password, driver-class-name)
     * from {@code application.properties} using the prefix {@code app.datasource.mysql}.
     *
     * <p>{@code @ConfigurationProperties} binds the property values automatically to
     * the fields of {@link DataSourceProperties}.
     *
     * @return a populated {@link DataSourceProperties} ready to build a {@link DataSource}
     */
    @Bean
    @ConfigurationProperties("app.datasource.mysql")
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Builds and exposes the MySQL {@link DataSource} as a Spring bean.
     *
     * <p>{@code @Primary} marks this as the default DataSource when multiple are present,
     * so Spring auto-wiring resolves to this one by default.
     *
     * @return a configured {@link DataSource} backed by the MySQL connection properties
     */
    @Primary
    @Bean(name = "mysqlDataSource")
    public DataSource dataSource() {
        // initializeDataSourceBuilder() uses the bound properties to create a HikariCP connection pool
        return mysqlDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Creates and configures the JPA {@link LocalContainerEntityManagerFactoryBean} for MySQL.
     *
     * <p>The entity manager factory is the core JPA component that manages the lifecycle
     * of entity objects and coordinates with Hibernate (the JPA provider) for SQL generation.
     *
     * <p>Key configuration:
     * <ul>
     *   <li>{@code packages} - tells Hibernate which package to scan for {@code @Entity} classes</li>
     *   <li>{@code persistenceUnit} - names this unit "mysql" (useful when multiple databases are configured)</li>
     *   <li>{@code hibernate.hbm2ddl.auto = update} - automatically creates/updates tables to match entity definitions on startup</li>
     * </ul>
     *
     * @param builder    Spring's helper to fluently build a {@link LocalContainerEntityManagerFactoryBean}
     * @param dataSource the MySQL {@link DataSource} injected by name (via {@code @Qualifier})
     * @return the configured factory bean
     */
    @Primary
    @Bean(name = "mysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("mysqlDataSource") DataSource dataSource) {

        // Extra Hibernate properties passed at the persistence-unit level
        Map<String, Object> mysqlProperties = new HashMap<>();
        mysqlProperties.put("hibernate.hbm2ddl.auto", "update"); // Auto-update schema without data loss

        return builder
                .dataSource(dataSource)
                .packages("in.guvi.task.springbootmvc.model")  // Scan model package for @Entity classes
                .persistenceUnit("mysql")                       // Named persistence unit
                .properties(mysqlProperties)                    // Apply extra Hibernate settings
                .build();
    }

    /**
     * Creates the {@link PlatformTransactionManager} for the MySQL persistence unit.
     *
     * <p>The transaction manager handles BEGIN, COMMIT, and ROLLBACK operations for
     * all database interactions in the application. It is linked to the custom
     * EntityManagerFactory so that transactions are scoped correctly.
     *
     * @param entityManagerFactory the MySQL EntityManagerFactory (injected by name)
     * @return a {@link JpaTransactionManager} wired to the MySQL EMF
     */
    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
