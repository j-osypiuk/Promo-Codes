package com.promocodes.api.promocode.dto;

import com.promocodes.api.promocode.CodeType;

import java.time.LocalDate;

public record PromoCodeOutputDto(

        String code,
        LocalDate expireDate,
        long maxUsages,
        long totalUsages,
        String amount,
        String currency,
        CodeType codeType
) {
}
