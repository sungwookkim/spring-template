package com.product.infra.product.jpa.productOption;

import com.product.domain.entity.Product;
import com.product.domain.repository.productOption.ProductOptionRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ProductOptionJpaRepository extends Repository<Product, Long>, ProductOptionRepository {
}
