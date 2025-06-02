package com.product.infra.product.jpa;

import com.product.domain.entity.Product;
import com.product.domain.repository.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-product-test"})
@DataJpaTest
class ProductJpaRepositoryTest {
    @Autowired
    ProductRepository productJpaRepository;

    @Test
    @DisplayName("상품 저장")
    void product_save() {
        //give
        Product product = new Product("상품명", "상품상세", 1000);

        //when
        this.productJpaRepository.save(product);

        //then
        Assertions.assertTrue(product.getProductId() > 0);
    }
}
