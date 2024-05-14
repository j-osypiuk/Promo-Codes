package com.promocodes.api.purchase;

import com.promocodes.api.exception.ObjectNotFoundException;
import com.promocodes.api.product.Product;
import com.promocodes.api.product.ProductRepository;
import com.promocodes.api.product.ProductService;
import com.promocodes.api.promocode.CodeType;
import com.promocodes.api.promocode.PromoCode;
import com.promocodes.api.promocode.PromoCodeRepository;
import com.promocodes.api.purchase.dto.CurrencySalesReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    private PurchaseService purchaseService;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromoCodeRepository promoCodeRepository;
    @Mock
    private ProductService productService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseService(
                purchaseRepository,
                productRepository,
                promoCodeRepository,
                productService
        );
    }

    @Test
    void addPurchaseInsertsNewPurchaseWithoutDiscountIfPromoCodeWasNotIncluded() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                        .productId(productId)
                        .name("Water")
                        .price(new BigDecimal("5.00"))
                        .currency("PLN")
                        .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        purchaseService.addPurchase(productId, null);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Purchase> purchaseArgumentCaptor = ArgumentCaptor.forClass(Purchase.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository, never()).findById(any());
        verify(productService, never()).getProductDiscountPrice(any(), any());
        verify(promoCodeRepository, never()).save(any());
        verify(purchaseRepository).save(purchaseArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        Purchase capturedPurchase = purchaseArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPurchase.getProduct().getProductId()).isEqualTo(productId);
        assertThat(capturedPurchase.getRegularPrice()).isEqualTo(product.getPrice());
        assertThat(capturedPurchase.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(capturedPurchase.getTimestamp().toLocalDate()).isEqualTo(LocalDate.now());
    }


    @Test
    void addPurchaseInsertsNewPurchaseWithDiscountIfPromoCodeWasIncluded() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .productId(productId)
                .name("Water")
                .price(new BigDecimal("5.00"))
                .currency("PLN")
                .build();
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(10)
                .codeType(CodeType.PERCENTAGE)
                .build();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(promoCode.getCode())).willReturn(Optional.of(promoCode));
        given(productService.getProductDiscountPrice(productId, promoCode.getCode())).willReturn(Map.of("discountPrice", "3.75"));

        // when
        purchaseService.addPurchase(productId, promoCode.getCode());

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> productIdArgumentCaptor2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PromoCode> promoCodeArgumentCaptor = ArgumentCaptor.forClass(PromoCode.class);
        ArgumentCaptor<Purchase> purchaseArgumentCaptor = ArgumentCaptor.forClass(Purchase.class);

        verify(productRepository).findById(productIdArgumentCaptor1.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor1.capture());
        verify(productService).getProductDiscountPrice(productIdArgumentCaptor2.capture(), promoCodeIdArgumentCaptor2.capture());
        verify(promoCodeRepository).save(promoCodeArgumentCaptor.capture());
        verify(purchaseRepository).save(purchaseArgumentCaptor.capture());

        UUID capturedProductId1 = productIdArgumentCaptor1.getValue();
        String capturedPromoCodeId1 = promoCodeIdArgumentCaptor1.getValue();
        UUID capturedProductId2 = productIdArgumentCaptor2.getValue();
        String capturedPromoCodeId2 = promoCodeIdArgumentCaptor2.getValue();
        PromoCode capturedPromoCode = promoCodeArgumentCaptor.getValue();
        Purchase capturedPurchase = purchaseArgumentCaptor.getValue();

        assertThat(capturedProductId1).isEqualTo(productId);
        assertThat(capturedPromoCodeId1).isEqualTo(promoCode.getCode());
        assertThat(capturedProductId2).isEqualTo(productId);
        assertThat(capturedPromoCodeId2).isEqualTo(promoCode.getCode());
        assertThat(capturedPromoCode.getTotalUsages()).isEqualTo(11);
        assertThat(capturedPurchase.getProduct().getProductId()).isEqualTo(productId);
        assertThat(capturedPurchase.getRegularPrice()).isEqualTo(product.getPrice());
        assertThat(capturedPurchase.getDiscount()).isEqualTo(new BigDecimal("1.25"));
        assertThat(capturedPurchase.getTimestamp().toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void addPurchaseInsertsNewPurchaseWithoutDiscountIfIncludedPromoCodeWasInvalid() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .productId(productId)
                .name("Water")
                .price(new BigDecimal("5.00"))
                .currency("PLN")
                .build();
        PromoCode promoCode = PromoCode.builder()
                .code("Summer2024")
                .expireDate(LocalDate.now().plusYears(2))
                .currency("USD")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(10)
                .codeType(CodeType.PERCENTAGE)
                .build();
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(promoCode.getCode())).willReturn(Optional.of(promoCode));
        given(productService.getProductDiscountPrice(productId, promoCode.getCode()))
                .willReturn(Map.of("discountPrice", "5.00", "warning", "test"));

        // when
        purchaseService.addPurchase(productId, promoCode.getCode());

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor1 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> productIdArgumentCaptor2 = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor2 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Purchase> purchaseArgumentCaptor = ArgumentCaptor.forClass(Purchase.class);

        verify(productRepository).findById(productIdArgumentCaptor1.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor1.capture());
        verify(productService).getProductDiscountPrice(productIdArgumentCaptor2.capture(), promoCodeIdArgumentCaptor2.capture());
        verify(promoCodeRepository, never()).save(any());
        verify(purchaseRepository).save(purchaseArgumentCaptor.capture());

        UUID capturedProductId1 = productIdArgumentCaptor1.getValue();
        String capturedPromoCodeId1 = promoCodeIdArgumentCaptor1.getValue();
        UUID capturedProductId2 = productIdArgumentCaptor2.getValue();
        String capturedPromoCodeId2 = promoCodeIdArgumentCaptor2.getValue();
        Purchase capturedPurchase = purchaseArgumentCaptor.getValue();

        assertThat(capturedProductId1).isEqualTo(productId);
        assertThat(capturedPromoCodeId1).isEqualTo(promoCode.getCode());
        assertThat(capturedProductId2).isEqualTo(productId);
        assertThat(capturedPromoCodeId2).isEqualTo(promoCode.getCode());
        assertThat(capturedPurchase.getProduct().getProductId()).isEqualTo(productId);
        assertThat(capturedPurchase.getRegularPrice()).isEqualTo(product.getPrice());
        assertThat(capturedPurchase.getDiscount()).isEqualTo(BigDecimal.ZERO);
        assertThat(capturedPurchase.getTimestamp().toLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void addPurchaseThrowsObjectNotFoundExceptionIfProductWithGivenIdDoesNotExist() {
        // given
        UUID productId = UUID.randomUUID();
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> purchaseService.addPurchase(productId, null))
                .isInstanceOf(ObjectNotFoundException.class);

        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository, never()).findById(any());
        verify(productService, never()).getProductDiscountPrice(any(), any());
        verify(promoCodeRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());

        UUID capturedProductId = productIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
    }

    @Test
    void addPurchaseThrowsObjectNotFoundExceptionIfGivenPromoCodeDoesNotExist() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";
        given(productRepository.findById(productId)).willReturn(Optional.of(Product.builder().productId(productId).build()));
        given(promoCodeRepository.findById(code)).willReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> purchaseService.addPurchase(productId, code))
                .isInstanceOf(ObjectNotFoundException.class);

        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);


        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());
        verify(productService, never()).getProductDiscountPrice(any(), any());
        verify(promoCodeRepository, never()).save(any());
        verify(purchaseRepository, never()).save(any());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
    }

    @Test
    void getPurchaseReportReturnsReport() {
        // given
        List<Purchase> purchases = Arrays.asList(
                Purchase.builder().
                        regularPrice(new BigDecimal("5.50"))
                        .discount(new BigDecimal("1.5"))
                        .product(Product.builder().currency("PLN").build())
                        .build(),
                Purchase.builder().
                        regularPrice(new BigDecimal("10.35"))
                        .discount(new BigDecimal("0.00"))
                        .product(Product.builder().currency("PLN").build())
                        .build(),
                Purchase.builder().
                        regularPrice(new BigDecimal("7.00"))
                        .discount(new BigDecimal("3.20"))
                        .product(Product.builder().currency("PLN").build())
                        .build(),
                Purchase.builder().
                        regularPrice(new BigDecimal("20.00"))
                        .discount(new BigDecimal("0.00"))
                        .product(Product.builder().currency("EUR").build())
                        .build(),
                Purchase.builder().
                        regularPrice(new BigDecimal("10.00"))
                        .discount(new BigDecimal("2.75"))
                        .product(Product.builder().currency("USD").build())
                        .build(),
                Purchase.builder().
                        regularPrice(new BigDecimal("7.00"))
                        .discount(new BigDecimal("0.00"))
                        .product(Product.builder().currency("USD").build())
                        .build()
        );
        given(purchaseRepository.findAll()).willReturn(purchases);

        // when
        List<CurrencySalesReportDto> report = purchaseService.getPurchaseReport();

        // then
        verify(purchaseRepository).findAll();

        assertThat(report.size()).isEqualTo(3);

        for (CurrencySalesReportDto csrd : report) {
            switch (csrd.currency()){
                case "PLN" -> {
                    assertThat(csrd.totalAmount()).isEqualTo("18.15");
                    assertThat(csrd.totalDiscount()).isEqualTo("4.70");
                    assertThat(csrd.noOfPurchases()).isEqualTo(3);
                }
                case "EUR" -> {
                    assertThat(csrd.totalAmount()).isEqualTo("20.00");
                    assertThat(csrd.totalDiscount()).isEqualTo("0.00");
                    assertThat(csrd.noOfPurchases()).isEqualTo(1);
                }
                case "USD" -> {
                    assertThat(csrd.totalAmount()).isEqualTo("14.25");
                    assertThat(csrd.totalDiscount()).isEqualTo("2.75");
                    assertThat(csrd.noOfPurchases()).isEqualTo(2);
                }
            }
        }
    }
}