package com.example.hello.service;

import com.example.hello.model.Product;
import com.example.hello.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsBySeller(com.example.hello.user.User seller) {
        return productRepository.findBySeller(seller);
    }

    public void updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId()).orElseThrow();
        existingProduct.setTitle(product.getTitle());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setDescription(product.getDescription());
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            existingProduct.setImageUrl(product.getImageUrl());
        }
        productRepository.save(existingProduct);
    }
}
