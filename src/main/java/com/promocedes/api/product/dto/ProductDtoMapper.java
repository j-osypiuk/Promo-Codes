package com.promocedes.api.product.dto;

import com.promocedes.api.product.Product;

public class ProductDtoMapper {

    public static Product mapProductInputDtoToProduct(ProductInputDto productInputDto) {

        return Product.builder()
                .name(productInputDto.name())
                .description(productInputDto.description())
                .price(productInputDto.price())
                .currency(productInputDto.currency())
                .build();
    }
}
