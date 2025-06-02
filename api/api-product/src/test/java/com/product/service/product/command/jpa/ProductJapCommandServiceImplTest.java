package com.product.service.product.command.jpa;

import com.product.domain.entity.Category;
import com.product.domain.entity.Product;
import com.product.domain.entity.ProductOption;
import com.product.domain.entity.Stock;
import com.product.service.product.command.ProductCommandService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-product-test"})
@SpringBootTest
public class ProductJapCommandServiceImplTest {
    @Autowired
    ProductCommandService productJapCommandServiceImpl;

    @Test
    @DisplayName("카테고리 + 상품 + 상품 옵션 + 수량 저장")
    void saveCategoryAndProductAndProductOptionAndStock() {
        //give
        Category category = new Category("가전");
        Product product = new Product("우리집 TV", "우리집에서 만든 TV", 1000);
        ProductOption productOption = new ProductOption("색깔", "BLACK");
        Stock stock = new Stock(100);

        productOption.setStock(stock);
        product.addProductOption(productOption);
        category.addProduct(product);

        // when
        this.productJapCommandServiceImpl.saveCategoryAndProductAndProductOptionAndStock(category);

        //then
        Assertions.assertTrue(category.getCategoryId() > 0
                && product.getProductId() > 0
                && productOption.getProductOptionId() > 0
                && stock.getStockId() > 0);
    }
}
