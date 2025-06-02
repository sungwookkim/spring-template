package com.product.infra.product.mybatis;

import com.product.QueryExecute;
import com.product.domain.entity.Category;
import com.product.domain.repository.cateogry.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;

@ActiveProfiles(value = {"api-product-test"})
@SpringBootTest
public class CategoryMybatisRepositoryTest {
    @Autowired
    CategoryRepository categoryMybatisRepository;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from category");
    }

    @Test
    @DisplayName("카테고리 저장")
    void category_save() {
        //give
        Category category = new Category("가전");

        //when
        this.categoryMybatisRepository.save(category);

        //then
        Assertions.assertTrue(category.getCategoryId() > 0);
    }
}
