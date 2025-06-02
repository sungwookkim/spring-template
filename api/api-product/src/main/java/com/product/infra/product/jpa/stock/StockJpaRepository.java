package com.product.infra.product.jpa.stock;

import com.product.domain.entity.Product;
import com.product.domain.repository.stock.StockRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface StockJpaRepository extends Repository<Product, Long>, StockRepository {
}
