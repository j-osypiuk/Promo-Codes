package com.promocedes.api.promocode.dto;

import com.promocedes.api.promocode.CodeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record PromoCodeInputDto(

        @Length(
                min = 3,
                max = 24,
                message = "Promo code must be a text with 3-24 alphanumeric case-sensitive characters"
        )
        String code,
        @NotNull(message = "Promo code expiration date cannot be blank")
        @Future(message = "Promo code expiration date must be in future")
        LocalDate expireDate,
        @Positive(message = "Max amount of usages of promo code must be a positive number")
        long maxUsages,
        @Positive(message = "Amount of discount promo code must be a positive number")
        double amount,
        @Length(
                min = 3,
                max = 3,
                message = "Promo code must discount currency must be a 3 characters long text"
        )
        String currency,
        @NotNull(message = "Code type must have a value 'QUANTITATIVE' or 'PERCENTAGE'")
        CodeType codeType
) {
}
