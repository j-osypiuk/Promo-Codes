package com.promocodes.api.product.dto;

import com.promocodes.api.product.Product;
import com.promocodes.api.utils.DecimalFormatter;

import java.math.BigDecimal;

public class ProductDtoMapper {

    public static Product mapProductInputDtoToProduct(ProductInputDto productInputDto) {

        return Product.builder()
                .name(productInputDto.name())
                .description(productInputDto.description())
                .price(new BigDecimal(productInputDto.price()))
                .currency(productInputDto.currency())
                .build();
    }

    public static ProductOutputDto mapProductToProductOutputDto(Product product) {

        return new ProductOutputDto(
                product.getProductId(),
                product.getName(),
                product.getDescription() == null ? null : product.getDescription(),
                DecimalFormatter.formatToTwoDecimalPoints(product.getPrice()),
                product.getCurrency()
        );
    }
}
