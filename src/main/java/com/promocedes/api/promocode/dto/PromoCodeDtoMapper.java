package com.promocedes.api.promocode.dto;

import com.promocedes.api.promocode.PromoCode;

public class PromoCodeDtoMapper {

    public static PromoCode mapPromoCodeInputDtoToPromoCode(PromoCodeInputDto promoCodeInputDto){
        return PromoCode.builder()
                .code(promoCodeInputDto.code())
                .expireDate(promoCodeInputDto.expireDate())
                .maxUsages(promoCodeInputDto.maxUsages())
                .totalUsages(0)
                .amount(promoCodeInputDto.amount())
                .currency(promoCodeInputDto.currency())
                .build();
    }
}
