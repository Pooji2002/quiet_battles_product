package com.quietbattles.product_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.quietbattles.product_service.dto.ProductRequest;
import com.quietbattles.product_service.dto.ProductResponse;
import com.quietbattles.product_service.entity.Product;
import com.quietbattles.product_service.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Product SKU already exists");
        }

        Product product = new Product();
        updateProductFields(product, request);

        return convertToResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return convertToResponse(getProduct(id));
    }

    public ProductResponse updateProduct(
            Long id, ProductRequest request) {

        Product product = getProduct(id);

        if (productRepository.existsBySkuIgnoreCaseAndIdNot(
                request.getSku(), id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Product SKU already exists");
        }

        updateProductFields(product, request);

        return convertToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.delete(getProduct(id));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }

    private void updateProductFields(
            Product product, ProductRequest request) {

        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setStatus(request.getStatus());
    }

    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
    
    public ProductResponse reserveStock(Long productId, Integer quantity) {

        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }

        int updatedRows = productRepository.reserveStock(productId, quantity);

        if (updatedRows == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient stock or product is inactive");
        }

        return convertToResponse(getProduct(productId));
    }

    public ProductResponse releaseStock(Long productId, Integer quantity) {

        int updatedRows = productRepository.releaseStock(productId, quantity);

        if (updatedRows == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Product not found");
        }

        return convertToResponse(getProduct(productId));
    }
}