package com.product.infra.product.mybatis;

import com.product.QueryExecute;
import com.product.domain.entity.Product;
import com.product.domain.entity.ProductOption;
import com.product.domain.repository.product.ProductRepository;
import com.product.domain.repository.productOption.ProductOptionRepository;
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
public class ProductOptionMybatisRepositoryTest {
    @Autowired
    ProductOptionRepository productOptionMybatisRepository;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from product_option");
    }

    @Test
    @DisplayName("상품 옵션 저장")
    void productOption_save() {
        //give
        ProductOption productOption = new ProductOption("색깔", "BLACK");

        //when
        this.productOptionMybatisRepository.save(productOption);

        //then
        Assertions.assertTrue(productOption.getProductOptionId() > 0);
    }
}
