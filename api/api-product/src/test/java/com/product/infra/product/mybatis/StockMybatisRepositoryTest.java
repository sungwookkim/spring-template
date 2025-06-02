package com.product.infra.product.mybatis;

import com.product.QueryExecute;
import com.product.domain.entity.ProductOption;
import com.product.domain.entity.Stock;
import com.product.domain.repository.productOption.ProductOptionRepository;
import com.product.domain.repository.stock.StockRepository;
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
public class StockMybatisRepositoryTest {
    @Autowired
    StockRepository stockMybatisRepository;

    @BeforeEach
    void setUp() throws SQLException {
        QueryExecute.execute("delete from stock");
    }

    @Test
    @DisplayName("수량 저장")
    void stock_save() {
        //give
        Stock stock = new Stock(100);

        //when
        this.stockMybatisRepository.save(stock);

        //then
        Assertions.assertTrue(stock.getStockId() > 0);
    }
}
