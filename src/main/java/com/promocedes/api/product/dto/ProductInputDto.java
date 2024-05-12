package com.promocedes.api.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductInputDto(

        @NotBlank(message = "Product name cannot be blank")
        String name,
        String description,
        @Positive(message = "Product price must be a positive number")
        double price,
        @NotBlank(message = "Product price currency cannot be blank")
        String currency
) {
}
