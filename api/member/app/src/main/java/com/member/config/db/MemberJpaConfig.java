package com.member.config.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.member.infra.*.jpa"
    , entityManagerFactoryRef = "memberEntityManagerFactory"
    , transactionManagerRef = "memberTransactionManager")
public class MemberJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean memberEntityManagerFactory(DataSource memberDatasource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(memberDatasource);
        em.setPackagesToScan("com.member.domain.entity");
        em.setPersistenceUnitName("memberPersistenceUnit");

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(properties);

        return em;
    }
}
