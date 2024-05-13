package com.promocedes.api.product;

import com.promocedes.api.exception.DuplicateUniqueValueException;
import com.promocedes.api.exception.InvalidValueException;
import com.promocedes.api.exception.ObjectNotFoundException;
import com.promocedes.api.promocode.CodeType;
import com.promocedes.api.promocode.PromoCode;
import com.promocedes.api.promocode.PromoCodeRepository;
import com.promocedes.api.utils.DecimalFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;

    public Product addProduct(Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidValueException("Product price must be a positive number");

        if (productRepository.findByName(product.getName()).isPresent())
            throw new DuplicateUniqueValueException("Product with given name already exists");

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProductById(UUID productId, Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidValueException("Product price must be a positive number");

        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product with id = " + productId + " does not exist"));

        if (!product.getName().equals(productDB.getName()))
            if (productRepository.findByName(product.getName()).isPresent())
                throw new DuplicateUniqueValueException("Product with given name already exists");

        productDB.setName(product.getName());
        productDB.setDescription(product.getDescription());
        productDB.setPrice(product.getPrice());
        productDB.setCurrency(product.getCurrency());

        return productRepository.save(productDB);
    }

    public Map<String, String> getProductDiscountPrice(UUID productId, String code) {
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new ObjectNotFoundException("Product with id = " + productId + " does not exist"));

        PromoCode promoCodeDB = promoCodeRepository.findById(code)
                .orElseThrow(() -> new ObjectNotFoundException("Promo code: '" + code + "' does not exists"));

        Map<String, String> discountPriceMap = new HashMap<>();

        if (promoCodeDB.getExpireDate().isBefore(LocalDate.now())) {
            discountPriceMap.put("discountPrice", "" + productDB.getPrice());
            discountPriceMap.put("warning", "Promo code usage time expired");
            return discountPriceMap;
        }

        if (!promoCodeDB.getCurrency().equals(productDB.getCurrency())) {
            discountPriceMap.put("discountPrice", "" + productDB.getPrice());
            discountPriceMap.put("warning", "Promo code currency does not match product price currency");
            return discountPriceMap;
        }

        if (promoCodeDB.getTotalUsages() >= promoCodeDB.getMaxUsages()) {
            discountPriceMap.put("discountPrice", "" + productDB.getPrice());
            discountPriceMap.put("warning", "The number of possible uses of the promo code has been exhausted");
            return discountPriceMap;
        }

        BigDecimal discountPrice;

        if (promoCodeDB.getCodeType() == CodeType.QUANTITATIVE)
            discountPrice = productDB.getPrice().subtract(promoCodeDB.getAmount());
        else
            discountPrice = productDB.getPrice().subtract(productDB.getPrice()
                    .multiply(promoCodeDB.getAmount().divide(new BigDecimal(("100.00")), RoundingMode.HALF_UP)));

        if (discountPrice.compareTo(BigDecimal.ZERO) < 0)
            discountPrice = BigDecimal.ZERO;

        discountPriceMap.put("discountPrice", DecimalFormatter.formatToTwoDecimalPoints(discountPrice));

        return discountPriceMap;
    }
}
