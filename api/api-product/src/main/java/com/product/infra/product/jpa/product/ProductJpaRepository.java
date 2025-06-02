package com.product.infra.product.jpa.product;

import com.product.domain.entity.Product;
import com.product.domain.repository.product.ProductRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface ProductJpaRepository extends Repository<Product, Long>, ProductRepository {

}
