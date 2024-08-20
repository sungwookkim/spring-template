package com.member.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@Getter
public class MemberDBConfig {

    /**
     * DataSource 객체 반환 클래스
     */
    @Configuration
    @Getter
    public static class MemberDatasourceConfig {
        private final String driverClassName;
        private final String url;
        private final String username;
        private final String password;

        public MemberDatasourceConfig(@Value("${spring.datasource.h2.driver-class-name}")String driverClassName
                , @Value("${spring.datasource.h2.url}") String url
                , @Value("${spring.datasource.h2.username}") String username
                , @Value("${spring.datasource.h2.password}") String password) {
            this.driverClassName = driverClassName;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        @Bean
        public DataSource memberDatasource() {
            HikariDataSource hikariDataSource = new HikariDataSource();
            hikariDataSource.setPoolName("member-db");
            hikariDataSource.setDriverClassName(this.driverClassName);
            hikariDataSource.setJdbcUrl(this.url);
            hikariDataSource.setUsername(this.username);
            hikariDataSource.setPassword(this.password);

            return hikariDataSource;
        }
    }
}
