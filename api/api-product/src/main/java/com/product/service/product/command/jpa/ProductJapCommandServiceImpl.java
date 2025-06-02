package com.product.service.product.command.jpa;

import com.outboxEvent.domain.entity.OutboxEvent;
import com.outboxEvent.helper.MessageHelper;
import com.payload.ProductCreatedEventPayload;
import com.product.config.transactional.annotaion.ProductWriteTransactional;
import com.product.domain.entity.Category;
import com.product.domain.entity.Product;
import com.product.domain.repository.cateogry.CategoryRepository;
import com.product.service.outboxEvent.command.OutboxEventCommandService;
import com.product.service.product.command.ProductCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@ProductWriteTransactional
public class ProductJapCommandServiceImpl implements ProductCommandService {
    private final CategoryRepository categoryJpaRepository;
    private final OutboxEventCommandService outboxEventDecoratorServiceImpl;

    /**
     * 카테고리, 제품, 제품 옵션 및 재고 정보를 저장합니다.
     *
     * @param category 저장할 카테고리 엔티티 객체. 해당 객체에는 관련된 제품 리스트도 포함되어야 합니다.
     */
    @Override
    public void saveCategoryAndProductAndProductOptionAndStock(Category category) {
        this.categoryJpaRepository.save(category);

        Long categoryId = category.getCategoryId();
        List<Product> products = category.getProducts();

        List<OutboxEvent> outboxEvents = new ArrayList<>();
        for(Product product : products) {
            outboxEvents.add(OutboxEvent.create(Category.CLASS_NAME
                    , String.format("%s-%d-%d", MessageHelper.Kafka.MessageKey.EVENT_PRODUCT, categoryId, product.getProductId())
                    , MessageHelper.Kafka.SOURCE_SYSTEM
                    , MessageHelper.Kafka.MessageKey.EVENT_PRODUCT
                    , new ProductCreatedEventPayload(categoryId, product.getProductId())));
        }

        this.outboxEventDecoratorServiceImpl.save(outboxEvents);
    }
}
