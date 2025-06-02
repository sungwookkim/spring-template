package com.product.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
public class Product {
    public final static String CLASS_NAME = Product.class.getSimpleName();

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList<>();

    public Product(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void setCategory(Category category) {
        Optional.ofNullable(this.category)
                .ifPresent(c -> c.getProducts().remove(this));

        this.category = category;

        if(!category.getProducts().contains(this)) {
            category.getProducts().add(this);
        }
    }

    public void addProductOption(ProductOption productOption) {
        if(!this.productOptions.contains(productOption)) {
            this.productOptions.add(productOption);
        }

        if(productOption.getProduct() != this) {
            productOption.setProduct(this);
        }
    }
}