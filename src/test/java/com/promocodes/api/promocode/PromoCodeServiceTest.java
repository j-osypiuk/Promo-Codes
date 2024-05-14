package com.promocodes.api.promocode;

import com.promocodes.api.exception.DuplicateUniqueValueException;
import com.promocodes.api.exception.InvalidValueException;
import com.promocodes.api.exception.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PromoCodeServiceTest {

    private PromoCodeService promoCodeService;

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @BeforeEach
    void setUp() {
        promoCodeService = new PromoCodeService(promoCodeRepository);
    }

    @Test
    void addPromoCodeInsertsNewPromoCode() {
        // given
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .codeType(CodeType.PERCENTAGE)
                .build();

        // when
        promoCodeService.addPromoCode(promoCode);

        //then
        ArgumentCaptor<String> promoCodeIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PromoCode> promoCodeCaptor = ArgumentCaptor.forClass(PromoCode.class);

        verify(promoCodeRepository).findById(promoCodeIdCaptor.capture());
        verify(promoCodeRepository).save(promoCodeCaptor.capture());

        String capturedPromoCodeId = promoCodeIdCaptor.getValue();
        PromoCode capturedPromoCode = promoCodeCaptor.getValue();

        assertThat(capturedPromoCodeId).isEqualTo(promoCode.getCode());
        assertThat(capturedPromoCode).isEqualTo(promoCode);
    }

    @Test
    void addPromoCodeThrowsInvalidValueExceptionIfPromoCodeAmountIsZero() {
        // given
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("0.00"))
                .maxUsages(100)
                .codeType(CodeType.PERCENTAGE)
                .build();

        // when

        //then
        assertThatThrownBy(() -> promoCodeService.addPromoCode(promoCode))
                .isInstanceOf(InvalidValueException.class);

        verify(promoCodeRepository, never()).findById(any());
        verify(promoCodeRepository, never()).save(any());
    }

    @Test
    void addPromoCodeThrowsInvalidValueExceptionIfPromoCodeAmountIsNegative() {
        // given
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("-50.00"))
                .maxUsages(100)
                .codeType(CodeType.PERCENTAGE)
                .build();

        // when

        //then
        assertThatThrownBy(() -> promoCodeService.addPromoCode(promoCode))
                .isInstanceOf(InvalidValueException.class);

        verify(promoCodeRepository, never()).findById(any());
        verify(promoCodeRepository, never()).save(any());
    }

    @Test
    void addPromoCodeThrowsDuplicateUniqueValueExceptionIfPromoCodeWithSameIdAlreadyExists() {
        // given
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("5.00"))
                .maxUsages(100)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(promoCodeRepository.findById(promoCode.getCode())).willReturn(Optional.of(PromoCode.builder().build()));
        // when

        //then
        ArgumentCaptor<String> promoCodeIdCaptor = ArgumentCaptor.forClass(String.class);

        assertThatThrownBy(() -> promoCodeService.addPromoCode(promoCode))
                .isInstanceOf(DuplicateUniqueValueException.class);

        verify(promoCodeRepository).findById(promoCodeIdCaptor.capture());
        verify(promoCodeRepository, never()).save(any());

        String capturedPromoCodeId = promoCodeIdCaptor.getValue();

        assertThat(capturedPromoCodeId).isEqualTo(promoCode.getCode());
    }

    @Test
    void getAllPromoCodesInvokesFindAllMethod() {
        // given

        // when
        promoCodeService.getAllPromoCodes();

        // then
        verify(promoCodeRepository).findAll();
    }

    @Test
    void getPromoCodeReturnsPromoCodeWithGivenId() {
        // given
        String code = "Summer2024";

        given(promoCodeRepository.findById(code)).willReturn(Optional.of(PromoCode.builder().code(code).build()));

        // when
        PromoCode returnedPromoCode = promoCodeService.getPromoCode(code);

        // then
        ArgumentCaptor<String> promoCodeIdCaptor = ArgumentCaptor.forClass(String.class);

        verify(promoCodeRepository).findById(promoCodeIdCaptor.capture());

        String capturedPromoCodeId = promoCodeIdCaptor.getValue();

        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedPromoCode.getCode()).isEqualTo(code);
    }

    @Test
    void getPromoCodeThrowsObjectNotFoundExceptionIfPromoCodeWithGivenIdDoesNotExists() {
        // given
        String code = "Summer2024";

        given(promoCodeRepository.findById(code)).willReturn(Optional.empty());

        // when

        // then
        ArgumentCaptor<String> promoCodeIdCaptor = ArgumentCaptor.forClass(String.class);

        assertThatThrownBy(() -> promoCodeService.getPromoCode(code))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(promoCodeRepository).findById(promoCodeIdCaptor.capture());

        String capturedPromoCodeId = promoCodeIdCaptor.getValue();

        assertThat(capturedPromoCodeId).isEqualTo(code);
    }
}