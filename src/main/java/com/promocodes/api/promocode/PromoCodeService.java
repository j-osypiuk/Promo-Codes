package com.promocodes.api.promocode;

import com.promocodes.api.exception.DuplicateUniqueValueException;
import com.promocodes.api.exception.InvalidValueException;
import com.promocodes.api.exception.ObjectNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCode addPromoCode(PromoCode promoCode) {
        if (promoCode.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidValueException("Promo code amount must be a positive number");

        if (promoCodeRepository.findById(promoCode.getCode()).isPresent())
            throw new DuplicateUniqueValueException("Given promo code already exists");

        return promoCodeRepository.save(promoCode);
    }

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

    public PromoCode getPromoCode(String code) {
        return promoCodeRepository.findById(code)
                .orElseThrow(() -> new ObjectNotFoundException("Promo code: '" + code + "' does not exists"));
    }
}
