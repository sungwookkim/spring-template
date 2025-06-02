package com.product.infra.product.jpa.category;

import com.product.domain.entity.Product;
import com.product.domain.repository.cateogry.CategoryRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface CategoryJpaRepository extends Repository<Product, Long>, CategoryRepository {
}
