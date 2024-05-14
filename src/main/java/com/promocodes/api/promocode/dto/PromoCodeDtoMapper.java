package com.promocodes.api.promocode.dto;

import com.promocodes.api.promocode.PromoCode;
import com.promocodes.api.utils.DecimalFormatter;

import java.math.BigDecimal;

public class PromoCodeDtoMapper {

    public static PromoCode mapPromoCodeInputDtoToPromoCode(PromoCodeInputDto promoCodeInputDto){
        return PromoCode.builder()
                .code(promoCodeInputDto.code())
                .expireDate(promoCodeInputDto.expireDate())
                .maxUsages(promoCodeInputDto.maxUsages())
                .totalUsages(0)
                .amount(new BigDecimal(promoCodeInputDto.amount()))
                .currency(promoCodeInputDto.currency())
                .codeType(promoCodeInputDto.codeType())
                .build();
    }

    public static PromoCodeOutputDto mapPromoCodeToPromoCodeOutputDto(PromoCode promoCode) {
        return new PromoCodeOutputDto(
                promoCode.getCode(),
                promoCode.getExpireDate(),
                promoCode.getMaxUsages(),
                promoCode.getTotalUsages(),
                DecimalFormatter.formatToTwoDecimalPoints(promoCode.getAmount()),
                promoCode.getCurrency(),
                promoCode.getCodeType()
        );
    }
}
