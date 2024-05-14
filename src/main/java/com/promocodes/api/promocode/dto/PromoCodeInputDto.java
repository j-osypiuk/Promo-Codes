package com.promocodes.api.promocode.dto;

import com.promocodes.api.promocode.CodeType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PromoCodeInputDto(

        @NotBlank(message = "Promo code cannot be blank")
        @Pattern(
                regexp = "^[a-zA-Z0-9]{3,24}$",
                message = "Promo code must be a text with 3-24 alphanumeric case-sensitive characters which must not " +
                        "contain whitespaces"
        )
        String code,
        @NotNull(message = "Promo code expiration date cannot be blank")
        @Future(message = "Promo code expiration date must be in future")
        LocalDate expireDate,
        @Positive(message = "Max amount of usages of promo code must be a positive number")
        long maxUsages,
        @NotBlank(message = "Amount of discount promo code must be a positive number with two decimal points")
        @Pattern(
                regexp = "^[0-9]*[.]{1}[0-9]{2}$",
                message = "Amount of discount promo code must be a positive number with two decimal points"
        )
        String amount,
        @NotBlank(message = "Promo code currency cannot be blank")
        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Promo code currency must match ISO 4217 currency code"
        )
        String currency,
        @NotNull(message = "Code type must have a value 'QUANTITATIVE' or 'PERCENTAGE'")
        CodeType codeType
) {
}
