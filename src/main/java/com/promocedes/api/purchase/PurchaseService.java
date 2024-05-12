package com.promocedes.api.purchase;

import com.promocedes.api.exception.ObjectNotFoundException;
import com.promocedes.api.product.Product;
import com.promocedes.api.product.ProductRepository;
import com.promocedes.api.product.ProductService;
import com.promocedes.api.promocode.PromoCode;
import com.promocedes.api.promocode.PromoCodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final ProductService productService;

    public void addPurchase(UUID productId, String code) {
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product with id = " + productId + " does not exist"));

        PromoCode promoCodeDB = promoCodeRepository.findById(code)
                .orElseThrow(() -> new ObjectNotFoundException("Promo code: '" + code + "' does not exists"));

        Map<String, String> discount = productService.getProductDiscountPrice(productId, code);

        Purchase purchase = Purchase.builder()
                .product(productDB)
                .regularPrice(productDB.getPrice())
                .timestamp(LocalDateTime.now())
                .discount(productDB.getPrice() - Double.parseDouble(discount.get("discountPrice")))
                .build();

        purchaseRepository.save(purchase);
    }
}
