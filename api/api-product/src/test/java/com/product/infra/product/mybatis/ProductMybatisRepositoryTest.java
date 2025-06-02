package com.product.infra.product.mybatis;

import com.product.QueryExecute;
import com.product.domain.entity.Category;
import com.product.domain.entity.Product;
import com.product.domain.repository.cateogry.CategoryRepository;
import com.product.domain.repository.product.ProductRepository;
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
public class ProductMybatisRepositoryTest {
    @Autowired
    ProductRepository productMybatisRepository;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from product");
    }

    @Test
    @DisplayName("상품 저장")
    void product_save() {
        //give
        Product product = new Product("상품명", "상품상세", 1000);

        //when
        this.productMybatisRepository.save(product);

        //then
        Assertions.assertTrue(product.getProductId() > 0);
    }
}
