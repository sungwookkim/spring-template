package com.product.infra.product.mybatis.stock;

import com.product.domain.entity.Stock;
import com.product.domain.repository.stock.StockRepository;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface StockMybatisRepository extends StockRepository {
    @Insert("""
        insert into stock (
            quantity
        ) values (
            #{quantity}
        )
        ;
    """)
    @Options(useGeneratedKeys = true, keyColumn = "stock_id", keyProperty = "stockId")
    @Override
    void save(Stock stock);
}
