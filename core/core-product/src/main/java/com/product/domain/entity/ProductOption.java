package com.product.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor
public class ProductOption {
    public final static String CLASS_NAME = ProductOption.class.getSimpleName();

    @Id
    @Column(name = "product_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOptionId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(mappedBy = "productOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private Stock stock;

    public ProductOption(String optionName, String optionValue) {
        this.optionName = optionName;
        this.optionValue = optionValue;
    }

    public void setProduct(Product product) {
        Optional.ofNullable(this.product)
                .ifPresent(c -> c.getProductOptions().remove(this));

        this.product = product;

        if(!product.getProductOptions().contains(this)) {
            product.getProductOptions().add(this);
        }
    }
}
