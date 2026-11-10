package com.quietbattles.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quietbattles.product_service.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity - :quantity
        WHERE p.id = :productId
          AND p.stockQuantity >= :quantity
          AND p.status = 'ACTIVE'
    """)
    int reserveStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity);

    @Modifying
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity + :quantity
        WHERE p.id = :productId
    """)
    int releaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity);
}