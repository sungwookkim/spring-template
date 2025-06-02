package com.member.config.db;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.member.infra.*.mybatis"}, sqlSessionFactoryRef = "memberSessionFactory")
public class MemberMybatisConfig {

    @Bean
    public SqlSessionFactory memberSessionFactory(DataSource memberDatasource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(memberDatasource);
        sqlSessionFactoryBean.setTypeAliasesPackage("com.member.infra.mybatis");

        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate memberSessionTemplate(SqlSessionFactory memberSessionFactory) throws Exception {
        return new SqlSessionTemplate(memberSessionFactory);
    }
}
