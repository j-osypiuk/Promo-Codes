package com.promocedes.api.promocode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    private String code;
    @Column(
            nullable = false
    )
    private LocalDate expireDate;
    private long maxUsages;
    private long totalUsages;
    private double amount;
    @Column(
            nullable = false
    )
    private String currency;
}
