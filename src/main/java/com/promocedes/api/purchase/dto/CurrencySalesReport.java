package com.promocedes.api.purchase.dto;

public record CurrencySalesReport(
        String currency,
        double totalAmount,
        double totalDiscount,
        long noOfPurchases
) {
}
