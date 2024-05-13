package com.promocedes.api.product.dto;

import com.promocedes.api.product.Product;
import com.promocedes.api.utils.DecimalFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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
