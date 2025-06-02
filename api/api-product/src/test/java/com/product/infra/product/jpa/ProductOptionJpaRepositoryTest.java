package com.product.infra.product.jpa;

import com.product.domain.entity.ProductOption;
import com.product.domain.repository.productOption.ProductOptionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-product-test"})
@DataJpaTest
class ProductOptionJpaRepositoryTest {
    @Autowired
    ProductOptionRepository productOptionJpaRepository;

    @Test
    @DisplayName("상품 옵션 저장")
    void productOption_save() {
        //give
        ProductOption productOption = new ProductOption("색깔", "BLACK");

        //when
        this.productOptionJpaRepository.save(productOption);

        //then
        Assertions.assertTrue(productOption.getProductOptionId() > 0);
    }
}
