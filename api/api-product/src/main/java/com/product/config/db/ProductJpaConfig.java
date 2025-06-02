package com.product.config.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"com.product.infra"}
    , entityManagerFactoryRef = "productEntityManagerFactory"
    , transactionManagerRef = "productTransactionManager")
public class ProductJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean productEntityManagerFactory(DataSource productDatasource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(productDatasource);
        em.setPackagesToScan("com.product.domain.entity", "com.outboxEvent.domain.entity");
        em.setPersistenceUnitName("productPersistenceUnit");

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(properties);

        return em;
    }
}
