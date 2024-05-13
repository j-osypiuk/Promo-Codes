package com.promocedes.api.purchase.dto;

public record CurrencySalesReportDto(
        String currency,
        String totalAmount,
        String totalDiscount,
        long noOfPurchases
) {
}
