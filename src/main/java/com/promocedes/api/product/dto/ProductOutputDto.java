package com.promocedes.api.product.dto;

import java.util.UUID;

public record ProductOutputDto(

        UUID productId,
        String name,
        String description,
        String price,
        String currency
) {
}
