package com.project.shopapp.repositories;

import com.project.shopapp.models.ProductImage;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE (:productId IS NULL OR :productId = 0 OR pi.product.id = :productId)")
    Page<ProductImage> searchProductImages(@Param("productId") Long productId, Pageable pageable);
}
