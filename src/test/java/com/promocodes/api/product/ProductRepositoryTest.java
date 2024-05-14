package com.promocodes.api.product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest

class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    void findByNameReturnsProductWithGivenNameIfExists() {
        // given
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("2.50"))
                .currency("PLN")
                .build();

        productRepository.save(product);

        // when
        Optional<Product> testProduct = productRepository.findByName(product.getName());

        // then
        assertThat(testProduct.get()).isEqualTo(product);
    }

    @Test
    void findByNameReturnsEmptyOptionalIfProductWithGivenNameDoesNotExists() {
        // when
        Optional<Product> testProduct = productRepository.findByName("Name");

        //then
        assertThat(testProduct.isEmpty()).isTrue();
    }
}