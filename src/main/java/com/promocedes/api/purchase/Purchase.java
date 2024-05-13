package com.promocedes.api.purchase;

import com.promocedes.api.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID purchaseId;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false)
    private BigDecimal regularPrice;
    @Column(nullable = false)
    private BigDecimal discount;
    @ManyToOne
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "productId"
    )
    private Product product;
}
