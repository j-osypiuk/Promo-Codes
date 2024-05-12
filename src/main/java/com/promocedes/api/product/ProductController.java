package com.promocedes.api.product;

import com.promocedes.api.product.dto.ProductDtoMapper;
import com.promocedes.api.product.dto.ProductInputDto;
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
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(
                productService.getAllProducts(),
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
}
