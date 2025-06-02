package com.product.infra.product.mybatis.category;

import com.product.domain.entity.Category;
import com.product.domain.repository.cateogry.CategoryRepository;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface CategoryMybatisRepository extends CategoryRepository {

    @Insert("""
        insert into category (
            name
        ) values (
            #{name}
        )
        ;
    """)
    @Options(useGeneratedKeys = true, keyColumn = "category_id", keyProperty = "categoryId")
    @Override
    void save(Category category);
}
