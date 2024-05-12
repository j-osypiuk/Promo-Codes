package com.promocedes.api.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    private UUID productId;
    @Column(
            nullable = false,
            unique = true
    )
    private String name;
    private String description;
    private double price;
    @Column(
            nullable = false
    )
    private String currency;
}
