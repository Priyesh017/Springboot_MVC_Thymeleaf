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
 * Database configuration class for the PostgreSQL data source.
 *
 * <p>This class manually wires up the full JPA stack — DataSource, EntityManagerFactory,
 * and TransactionManager — using properties defined under the {@code app.datasource.postgres.*}
 * prefix in {@code application.properties}.
 *
 * <ul>
 * <li>{@code @Configuration}            - marks this as a Spring configuration class</li>
 * <li>{@code @EnableTransactionManagement} - activates annotation-driven transaction management</li>
 * <li>{@code @EnableJpaRepositories}    - tells Spring Data JPA which package to scan for repository interfaces</li>
 * </ul>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "in.guvi.task.springbootmvc.repository",      // Only scan this package for JPA repos
        entityManagerFactoryRef = "postgresEntityManagerFactory",    // Wire repos to our custom EM factory
        transactionManagerRef = "postgresTransactionManager"         // Wire repos to our custom TX manager
)
public class PostgresConfig {

    /**
     * Reads PostgreSQL connection properties (url, username, password, driver-class-name)
     * from {@code application.properties} using the prefix {@code app.datasource.postgres}.
     *
     * @return a populated {@link DataSourceProperties} ready to build a {@link DataSource}
     */
    @Bean
    @ConfigurationProperties("app.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Builds and exposes the PostgreSQL {@link DataSource} as a Spring bean.
     *
     * <p>{@code @Primary} marks this as the default DataSource when multiple are present.
     *
     * @return a configured {@link DataSource} backed by the PostgreSQL connection properties
     */
    @Primary
    @Bean(name = "postgresDataSource")
    public DataSource dataSource() {
        return postgresDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Creates and configures the JPA {@link LocalContainerEntityManagerFactoryBean} for PostgreSQL.
     *
     * @param builder    Spring's helper to fluently build a {@link LocalContainerEntityManagerFactoryBean}
     * @param dataSource the PostgreSQL {@link DataSource} injected by name (via {@code @Qualifier})
     * @return the configured factory bean
     */
    @Primary
    @Bean(name = "postgresEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("postgresDataSource") DataSource dataSource) {

        // Extra Hibernate properties passed at the persistence-unit level
        Map<String, Object> postgresProperties = new HashMap<>();

        // Auto-update schema without data loss.
        // Note: For production, 'validate' or relying on Flyway/Liquibase is recommended over 'update'.
        postgresProperties.put("hibernate.hbm2ddl.auto", "update");

        // Optional: Specify dialect explicitly if you run into auto-detection issues
        // postgresProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("in.guvi.task.springbootmvc.model")  // Scan model package for @Entity classes
                .persistenceUnit("postgres")                   // Named persistence unit
                .properties(postgresProperties)                // Apply extra Hibernate settings
                .build();
    }

    /**
     * Creates the {@link PlatformTransactionManager} for the PostgreSQL persistence unit.
     *
     * @param entityManagerFactory the PostgreSQL EntityManagerFactory (injected by name)
     * @return a {@link JpaTransactionManager} wired to the PostgreSQL EMF
     */
    @Primary
    @Bean(name = "postgresTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("postgresEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}