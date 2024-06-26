package com.promocodes.api.product;

import com.promocodes.api.product.dto.ProductDtoMapper;
import com.promocodes.api.product.dto.ProductInputDto;
import com.promocodes.api.product.dto.ProductOutputDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Map<String, UUID>> addProduct(@Valid @RequestBody ProductInputDto productInputDto) {
        Product product = productService.addProduct(ProductDtoMapper.mapProductInputDtoToProduct(productInputDto));

        return new ResponseEntity<>(
                Map.of("productId", product.getProductId()),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductOutputDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        return new ResponseEntity<>(
                products.stream().map(ProductDtoMapper::mapProductToProductOutputDto).toList(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, UUID>> updateProductById(@PathVariable("id") UUID productId,
                                                               @Valid @RequestBody ProductInputDto productInputDto) {
        Product product = productService
                .updateProductById(productId, ProductDtoMapper.mapProductInputDtoToProduct(productInputDto));

        return new ResponseEntity<>(
                Map.of("productId", product.getProductId()),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "/{id}", params = {"code"})
    public ResponseEntity<Map<String, String>> getProductDiscountPrice(@PathVariable("id") UUID productId,
                                                                       @RequestParam("code") String code) {

        return new ResponseEntity<>(
                productService.getProductDiscountPrice(productId, code),
                HttpStatus.OK
        );
    }
}
