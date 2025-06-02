package com.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@ComponentScan(basePackages = {"com.product", "com.outboxEvent"})
public class ProductAppApplicationTest {
    public static void main(String[] args) {
        SpringApplication.run(ProductAppApplicationTest.class, args);
    }
}
