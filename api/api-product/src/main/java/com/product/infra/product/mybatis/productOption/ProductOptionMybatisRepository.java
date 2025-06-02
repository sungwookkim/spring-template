package com.product.infra.product.mybatis.productOption;

import com.product.domain.entity.ProductOption;
import com.product.domain.repository.productOption.ProductOptionRepository;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ProductOptionMybatisRepository extends ProductOptionRepository {
    @Insert("""
        insert into product_option (
            option_name
            , option_value
        ) values (
            #{optionName}
            , #{optionValue}
        )
        ;
    """)
    @Options(useGeneratedKeys = true, keyColumn = "product_option_id", keyProperty = "productOptionId")
    @Override
    void save(ProductOption productOption);
}
