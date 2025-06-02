package com.product.infra.product.mybatis.product;

import com.product.domain.entity.Product;
import com.product.domain.repository.product.ProductRepository;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ProductMybatisRepository extends ProductRepository {

    @Insert("""
        insert into product (
            name
            , description
            , price
        ) values (
            #{name}
            , #{description}
            , #{price}
        )
        ;
    """)
    @Options(useGeneratedKeys = true, keyColumn = "product_id", keyProperty = "productId")
    @Override
    void save(Product product);
}
