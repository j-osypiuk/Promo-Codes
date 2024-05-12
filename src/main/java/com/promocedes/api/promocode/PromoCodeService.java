package com.promocedes.api.promocode;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCode addPromoCode(PromoCode promoCode) {
        if (promoCodeRepository.findById(promoCode.getCode()).isPresent())
            throw new RuntimeException("Given promo code already exists");

        return promoCodeRepository.save(promoCode);
    }

    public List<PromoCode> getAllPromoCodes() {
        return promoCodeRepository.findAll();
    }

    public PromoCode getPromoCode(String code) {
        return promoCodeRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Promo code: '" + code + "' does not exists"));
    }
}
