package com.product.config.transactional;

import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@AllArgsConstructor
public class ProductAppTransactionalConfig {

    @Bean
    public PlatformTransactionManager productAppTransactionManager(EntityManagerFactory productAppEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(productAppEntityManagerFactory);

        return transactionManager;
    }
}
