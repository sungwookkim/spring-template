package com.product.service.product.command;

import com.product.domain.entity.Category;

/**
 * 제품 및 카테고리에 대한 명령 작업을 처리하는 서비스 인터페이스입니다.
 *
 * 이 인터페이스는 카테고리 및 관련 데이터(제품, 제품 옵션, 재고)를 저장하는 기능을 제공합니다.
 */
public interface ProductCommandService {

    /**
     * 주어진 카테고리(Category)를 저장하고, 해당 카테고리에 관련된 제품(Product),
     * 제품 옵션(ProductOption), 재고(Stock) 데이터를 함께 저장합니다.
     *
     * @param category 저장할 카테고리 객체
     */
    void saveCategoryAndProductAndProductOptionAndStock(Category category);
}
