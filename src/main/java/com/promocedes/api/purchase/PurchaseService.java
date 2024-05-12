package com.promocedes.api.purchase;

import com.promocedes.api.exception.ObjectNotFoundException;
import com.promocedes.api.product.Product;
import com.promocedes.api.product.ProductRepository;
import com.promocedes.api.product.ProductService;
import com.promocedes.api.promocode.PromoCodeRepository;
import com.promocedes.api.purchase.dto.CurrencySalesReport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        Map<String, String> discountMap;
        double discount = 0;

        if (code != null) {
            promoCodeRepository.findById(code)
                    .orElseThrow(() -> new ObjectNotFoundException("Promo code: '" + code + "' does not exists"));

            discountMap = productService.getProductDiscountPrice(productId, code);
            discount = productDB.getPrice() - Double.parseDouble(discountMap.get("discountPrice"));
        }

        Purchase purchase = Purchase.builder()
                .product(productDB)
                .regularPrice(productDB.getPrice())
                .timestamp(LocalDateTime.now())
                .discount(discount)
                .build();

        purchaseRepository.save(purchase);
    }

    public List<CurrencySalesReport> getPurchaseReport() {
        List<Purchase> purchases = purchaseRepository.findAll();

        List<String> currencies = purchases.stream().map(p -> p.getProduct().getCurrency()).distinct().toList();

        List<CurrencySalesReport> currencySalesReports = new ArrayList<>();

        for (String c : currencies) {
            List<Purchase> currencyPurchases = purchases.stream()
                    .filter(p -> p.getProduct().getCurrency().equals(c))
                    .toList();

            double totalAmount = 0;
            double totalDiscount = 0;

            for (Purchase p : currencyPurchases) {
                totalAmount += p.getRegularPrice() - p.getDiscount();
                totalDiscount += p.getDiscount();
            }

            currencySalesReports.add(new CurrencySalesReport(c, totalAmount, totalDiscount, currencyPurchases.size()));
        }

        return currencySalesReports;
    }
}
