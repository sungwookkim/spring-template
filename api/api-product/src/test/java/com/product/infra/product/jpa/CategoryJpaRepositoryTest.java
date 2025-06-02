package com.product.infra.product.jpa;

import com.product.domain.entity.Category;
import com.product.domain.repository.cateogry.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-product-test"})
@DataJpaTest
class CategoryJpaRepositoryTest {
    @Autowired
    CategoryRepository categoryJpaRepository;

    @Test
    @DisplayName("카테고리 저장")
    void category_save() {
        //give
        Category category = new Category("가전");

        //when
        this.categoryJpaRepository.save(category);

        //then
        Assertions.assertTrue(category.getCategoryId() > 0);
    }
}
