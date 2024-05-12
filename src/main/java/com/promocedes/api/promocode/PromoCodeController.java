package com.promocedes.api.promocode;

import com.promocedes.api.promocode.dto.PromoCodeDtoMapper;
import com.promocedes.api.promocode.dto.PromoCodeInputDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/codes")
@AllArgsConstructor
public class PromoCodeController {

    private final PromoCodeService promoCodeService;

    @PostMapping()
    public ResponseEntity<Map<String, String>> addPromoCode(@Valid @RequestBody PromoCodeInputDto promoCodeInputDto) {
        PromoCode promoCode = promoCodeService
                .addPromoCode(PromoCodeDtoMapper.mapPromoCodeInputDtoToPromoCode(promoCodeInputDto));

        return new ResponseEntity<>(
                Map.of("code", promoCode.getCode()),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<PromoCode>> getAllPromoCodes() {
        List<PromoCode> promoCodes = promoCodeService.getAllPromoCodes();

        return new ResponseEntity<>(
                promoCodes,
                HttpStatus.OK
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCode> getPromoCode(@PathVariable("code") String code) {
        return new ResponseEntity<>(
                promoCodeService.getPromoCode(code),
                HttpStatus.OK
        );
    }
}
