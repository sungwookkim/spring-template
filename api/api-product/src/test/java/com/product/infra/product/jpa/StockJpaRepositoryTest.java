package com.product.infra.product.jpa;

import com.product.domain.entity.Stock;
import com.product.domain.repository.stock.StockRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"api-product-test"})
@DataJpaTest
class StockJpaRepositoryTest {
    @Autowired
    StockRepository stockJpaRepository;

    @Test
    @DisplayName("수량 저장")
    void stock_save() {
        //give
        Stock stock = new Stock(100);

        //when
        this.stockJpaRepository.save(stock);

        //then
        Assertions.assertTrue(stock.getStockId() > 0);
    }
}
