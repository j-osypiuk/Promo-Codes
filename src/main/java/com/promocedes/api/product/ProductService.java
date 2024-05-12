package com.promocedes.api.product;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product addProduct(Product product) {
        if (productRepository.findByName(product.getName()).isPresent())
            throw new RuntimeException("Product with given name already exists");

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProductById(UUID productId, Product product) {
        Product productDB = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product with id = " + productId + "does not exist"));

        if (productRepository.findByName(product.getName()).isPresent())
            throw new RuntimeException("Product with given name already exists");

        productDB.setName(product.getName());
        productDB.setDescription(product.getDescription());
        productDB.setPrice(product.getPrice());
        productDB.setCurrency(product.getCurrency());

        return productRepository.save(productDB);
    }
}
