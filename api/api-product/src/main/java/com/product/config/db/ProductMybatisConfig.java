package com.product.config.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.product.infra.*.mybatis"}, sqlSessionFactoryRef = "productSessionFactory")
public class ProductMybatisConfig {

    @Bean
    public SqlSessionFactory productSessionFactory(DataSource productDatasource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(productDatasource);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.product.infra");

        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate productSessionTemplate(SqlSessionFactory productSessionFactory) throws Exception {
        return new SqlSessionTemplate(productSessionFactory);
    }
}
