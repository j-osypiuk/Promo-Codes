package com.promocodes.api.product;

import com.promocodes.api.exception.DuplicateUniqueValueException;
import com.promocodes.api.exception.InvalidValueException;
import com.promocodes.api.exception.ObjectNotFoundException;
import com.promocodes.api.promocode.CodeType;
import com.promocodes.api.promocode.PromoCode;
import com.promocodes.api.promocode.PromoCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PromoCodeRepository promoCodeRepository;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, promoCodeRepository);
    }

    @Test
    void addProductInsertsNewProduct() {
        // given
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("2.50"))
                .currency("PLN")
                .build();

        // when
        productService.addProduct(product);

        // then
        ArgumentCaptor<String> productNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).findByName(productNameArgumentCaptor.capture());
        verify(productRepository).save(productArgumentCaptor.capture());

        String capturedProductName = productNameArgumentCaptor.getValue();
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(capturedProductName).isEqualTo(product.getName());
        assertThat(capturedProduct).isEqualTo(product);
    }

    @Test
    void addProductThrowsInvalidArgumentExceptionIfProductPriceIsZero() {
        // given
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("0.00"))
                .currency("PLN")
                .build();

        // when

        // then
        assertThatThrownBy(() -> productService.addProduct(product))
                .isInstanceOf(InvalidValueException.class);

        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProductThrowsInvalidArgumentExceptionIfProductPriceIsNegativeNumber() {
        // given
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("-10.00"))
                .currency("PLN")
                .build();

        // when

        // then
        assertThatThrownBy(() -> productService.addProduct(product))
                .isInstanceOf(InvalidValueException.class);

        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void addProductThrowsDuplicateUniqueValueExceptionIfProductWithGivenNameAlreadyExists() {
        // given
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();
        given(productRepository.findByName(product.getName())).willReturn(Optional.of(Product.builder().build()));

        // when

        // then
        ArgumentCaptor<String> productNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        assertThatThrownBy(() -> productService.addProduct(product))
                .isInstanceOf(DuplicateUniqueValueException.class);

        verify(productRepository).findByName(productNameArgumentCaptor.capture());
        verify(productRepository, never()).save(any());

        String capturedProductName = productNameArgumentCaptor.getValue();
        assertThat(capturedProductName).isEqualTo(product.getName());
    }

    @Test
    void getAllProductsInvokesFindAllMethod() {
        // given

        // when
        productService.getAllProducts();

        // then
        verify(productRepository).findAll();
    }

    @Test
    void updateProductByIdUpdatesGivenProductData() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();
        given(productRepository.findById(productId)).willReturn(Optional.of(Product.builder().productId(productId).build()));

        // when
        productService.updateProductById(productId, product);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> productNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(productRepository).findByName(productNameArgumentCaptor.capture());
        verify(productRepository).save(productArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedProductName = productNameArgumentCaptor.getValue();
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedProductName).isEqualTo(product.getName());
        assertThat(capturedProduct.getName()).isEqualTo(product.getName());
        assertThat(capturedProduct.getDescription()).isEqualTo(product.getDescription());
        assertThat(capturedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(capturedProduct.getCurrency()).isEqualTo(product.getCurrency());
    }

    @Test
    void updateProductByIdInvalidArgumentExceptionIfProductPriceIsZero() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("0.00"))
                .currency("PLN")
                .build();

        // when

        // then
        assertThatThrownBy(() -> productService.updateProductById(productId, product))
                .isInstanceOf(InvalidValueException.class);

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductByIdInvalidArgumentExceptionIfProductPriceIsNegative() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("-6.00"))
                .currency("PLN")
                .build();

        // when

        // then
        assertThatThrownBy(() -> productService.updateProductById(productId, product))
                .isInstanceOf(InvalidValueException.class);

        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductByIdThrowsObjectNotFoundExceptionIfProductWithGivenIdDoesNotExists() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        assertThatThrownBy(() -> productService.updateProductById(productId, product))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(productRepository, never()).findByName(any());
        verify(productRepository, never()).save(any());

        UUID capturedProductId = productIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
    }

    @Test
    void updateProductByIdThrowsDuplicateUniqueValueExceptionIfProductWithGivenNameAlreadyExists() {
        // given
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();
        given(productRepository.findById(productId)).willReturn(Optional.of(Product.builder().productId(productId).build()));
        given(productRepository.findByName(product.getName())).willReturn(Optional.of(Product.builder().name("Water").build()));

        // when

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> productNameArgumentCaptor = ArgumentCaptor.forClass(String.class);

        assertThatThrownBy(() -> productService.updateProductById(productId, product))
                .isInstanceOf(DuplicateUniqueValueException.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(productRepository).findByName(productNameArgumentCaptor.capture());
        verify(productRepository, never()).save(any());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedProductName = productNameArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedProductName).isEqualTo(product.getName());
    }

    @Test
    void getProductDiscountPriceReturnsDiscountPriceForQuantitativePromoCodeType() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("5.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.QUANTITATIVE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo("5.00");
        assertThat(returnedDiscountMap.get("warning")).isNull();
    }

    @Test
    void getProductDiscountPriceReturnsDiscountPriceForPercentagePromoCodeType() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo("7.50");
        assertThat(returnedDiscountMap.get("warning")).isNull();
    }

    @Test
    void getProductDiscountPriceThrowsObjectNotFoundExceptionIfProductWithGivenIdDoesNotExists() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // when

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        assertThatThrownBy(() -> productService.getProductDiscountPrice(productId, code))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository, never()).findById(any());

        UUID capturedProductId = productIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
    }


    @Test
    void getProductDiscountPriceThrowsObjectNotFoundExceptionIfGivenCodeDoesNotExists() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        given(productRepository.findById(productId)).willReturn(Optional.of(Product.builder().productId(productId).build()));
        given(promoCodeRepository.findById(code)).willReturn(Optional.empty());

        // when

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        assertThatThrownBy(() -> productService.getProductDiscountPrice(productId, code))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
    }

    @Test
    void getProductDiscountPriceReturnsDiscountPriceForPercentagePromoCodeTypeIfExpireDateIsToday() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now())
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo("7.50");
        assertThat(returnedDiscountMap.get("warning")).isNull();
    }

    @Test
    void getProductDiscountPriceReturnsRegularPriceWithWarningIfPromoCodeIsExpired() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.of(2020, 10, 10))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo(product.getPrice().toString());
        assertThat(returnedDiscountMap.get("warning")).isNotNull();
    }

    @Test
    void getProductDiscountPriceReturnsRegularPriceWithWarningIfPromoCodeCurrencyAndProductPriceCurrencyDoesntMatch() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now().plusYears(2))
                .currency("USD")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo(product.getPrice().toString());
        assertThat(returnedDiscountMap.get("warning")).isNotNull();
    }

    @Test
    void getProductDiscountPriceReturnsRegularPriceWithWarningIfAllPromoCodeUsesHasBeenUsed() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(100)
                .codeType(CodeType.PERCENTAGE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo(product.getPrice().toString());
        assertThat(returnedDiscountMap.get("warning")).isNotNull();
    }

    @Test
    void getProductDiscountPriceReturnsZeroIfProductDiscountPriceIsNegativeOrZero() {
        // given
        UUID productId = UUID.randomUUID();
        String code = "Summer2024";

        Product product = Product.builder()
                .name("Water")
                .price(new BigDecimal("10.00"))
                .currency("PLN")
                .build();

        PromoCode promoCode = PromoCode.builder()
                .code(code)
                .expireDate(LocalDate.now().plusYears(2))
                .currency("PLN")
                .amount(new BigDecimal("25.00"))
                .maxUsages(100)
                .totalUsages(50)
                .codeType(CodeType.QUANTITATIVE)
                .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(promoCodeRepository.findById(code)).willReturn(Optional.of(promoCode));

        // when
        Map<String, String> returnedDiscountMap = productService.getProductDiscountPrice(productId, code);

        // then
        ArgumentCaptor<UUID> productIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> promoCodeIdArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(productRepository).findById(productIdArgumentCaptor.capture());
        verify(promoCodeRepository).findById(promoCodeIdArgumentCaptor.capture());

        UUID capturedProductId = productIdArgumentCaptor.getValue();
        String capturedPromoCodeId = promoCodeIdArgumentCaptor.getValue();

        assertThat(capturedProductId).isEqualTo(productId);
        assertThat(capturedPromoCodeId).isEqualTo(code);
        assertThat(returnedDiscountMap.get("discountPrice")).isEqualTo("0.00");
        assertThat(returnedDiscountMap.get("warning")).isNull();
    }
}