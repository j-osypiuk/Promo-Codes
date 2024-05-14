package com.promocodes.api.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ProductInputDto(

        @NotBlank(message = "Product name cannot be blank")
        String name,
        String description,
        @NotBlank(message = "Product price must be a positive number with two decimal points")
        @Pattern(
                regexp = "^[0-9]*[.]{1}[0-9]{2}$",
                message = "Product price must be a positive number with two decimal points"
        )
        String price,
        @NotBlank(message = "Product price currency cannot be blank")
        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Product price currency cannot be blank and must match ISO 4217 currency code"
        )
        String currency
) {
}
