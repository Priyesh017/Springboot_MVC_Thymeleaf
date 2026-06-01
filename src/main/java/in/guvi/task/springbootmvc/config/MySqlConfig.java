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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "in.guvi.task.springbootmvc.repository", // Where MySQL repos live
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager"
)
public class MySqlConfig {

    @Bean
    @ConfigurationProperties("app.datasource.mysql")
    public DataSourceProperties mysqlDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "mysqlDataSource") // customizing the method name
//    @ConfigurationProperties(prefix = "app.datasource.mysql")
    public DataSource dataSource() {
        return mysqlDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Primary
    @Bean(name = "mysqlEntityManagerFactory") // using bean annotation we are giving the entity manager object name
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("mysqlDataSource") DataSource dataSource) {
        Map<String, Object> mysqlProperties = new HashMap<>();
        mysqlProperties.put("hibernate.hbm2ddl.auto", "update");
        return builder
                .dataSource(dataSource)
                .packages("in.guvi.task.springbootmvc.model") // Where MySQL entities live
                .persistenceUnit("mysql") // in Jpa level it will store the entity manager object
                .properties(mysqlProperties)
                .build();
    }

    @Primary
    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
