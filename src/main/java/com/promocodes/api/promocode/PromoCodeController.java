package com.promocodes.api.promocode;

import com.promocodes.api.promocode.dto.PromoCodeDtoMapper;
import com.promocodes.api.promocode.dto.PromoCodeInputDto;
import com.promocodes.api.promocode.dto.PromoCodeOutputDto;
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
    public ResponseEntity<List<PromoCodeOutputDto>> getAllPromoCodes() {
        List<PromoCode> promoCodes = promoCodeService.getAllPromoCodes();

        return new ResponseEntity<>(
                promoCodes.stream().map(PromoCodeDtoMapper::mapPromoCodeToPromoCodeOutputDto).toList(),
                HttpStatus.OK
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<PromoCodeOutputDto> getPromoCode(@PathVariable("code") String code) {
        PromoCode promoCode = promoCodeService.getPromoCode(code);

        return new ResponseEntity<>(
                PromoCodeDtoMapper.mapPromoCodeToPromoCodeOutputDto(promoCode),
                HttpStatus.OK
        );
    }
}
