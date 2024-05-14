package com.promocedes.api.purchase;

import com.promocedes.api.exception.ObjectNotFoundException;
import com.promocedes.api.product.Product;
import com.promocedes.api.product.ProductRepository;
import com.promocedes.api.product.ProductService;
import com.promocedes.api.promocode.PromoCode;
import com.promocedes.api.promocode.PromoCodeRepository;
import com.promocedes.api.purchase.dto.CurrencySalesReportDto;
import com.promocedes.api.utils.DecimalFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        BigDecimal discount = BigDecimal.ZERO;

        if (code != null) {
            PromoCode promoCode = promoCodeRepository.findById(code)
                    .orElseThrow(() -> new ObjectNotFoundException("Promo code: '" + code + "' does not exists"));

            discountMap = productService.getProductDiscountPrice(productId, code);

            if (discountMap.get("warning") == null) {
                discount = productDB.getPrice().subtract(new BigDecimal(discountMap.get("discountPrice")));
                promoCode.setTotalUsages(promoCode.getTotalUsages() + 1);
                promoCodeRepository.save(promoCode);
            }
        }

        Purchase purchase = Purchase.builder()
                .product(productDB)
                .regularPrice(productDB.getPrice())
                .timestamp(LocalDateTime.now())
                .discount(discount)
                .build();

        purchaseRepository.save(purchase);
    }

    public List<CurrencySalesReportDto> getPurchaseReport() {
        List<Purchase> purchases = purchaseRepository.findAll();

        List<String> currencies = purchases.stream().map(p -> p.getProduct().getCurrency()).distinct().toList();

        List<CurrencySalesReportDto> currencySalesReportDtos = new ArrayList<>();

        for (String c : currencies) {
            List<Purchase> currencyPurchases = purchases.stream()
                    .filter(p -> p.getProduct().getCurrency().equals(c))
                    .toList();

            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            for (Purchase p : currencyPurchases) {
                totalAmount = totalAmount.add(p.getRegularPrice().subtract(p.getDiscount()));
                totalDiscount = totalDiscount.add(p.getDiscount());
            }

            currencySalesReportDtos.add(
                    new CurrencySalesReportDto(
                            c,
                            DecimalFormatter.formatToTwoDecimalPoints(totalAmount),
                            DecimalFormatter.formatToTwoDecimalPoints(totalDiscount),
                            currencyPurchases.size()
                    )
            );
        }

        return currencySalesReportDtos;
    }
}
