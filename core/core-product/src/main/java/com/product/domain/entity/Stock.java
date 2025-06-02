package com.product.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@Table(name = "stock")
@Getter
@NoArgsConstructor
public class Stock {
    public final static String CLASS_NAME = Stock.class.getSimpleName();

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id")
    private ProductOption productOption;

    public Stock(int quantity) {
        this.quantity = quantity;
    }

    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;

        if(productOption.getStock() != this) {
            productOption.setStock(this);
        }
    }
}
