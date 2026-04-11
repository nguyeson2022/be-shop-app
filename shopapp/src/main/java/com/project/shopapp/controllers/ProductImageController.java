package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;

import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.*;
import com.project.shopapp.responses.Product.ProductImageListResponse;
import com.project.shopapp.responses.Product.ProductImageResponse;
import com.project.shopapp.services.Product.ProductService;
import com.project.shopapp.services.image.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import java.util.List;


@RestController
@RequestMapping("${api.prefix}/product_images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(id);
        if (productImage != null) {
            productService.deleteFile(productImage.getImageUrl());
        }
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Delete product image successfully")
                        .data(productImage)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("")
    public ResponseEntity<ProductImageListResponse> getProducts(
            @RequestParam(defaultValue = "0", name = "productId") Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page - 1 , limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<ProductImageResponse> productImagePages = productImageService.getAllProductImages( productId, pageRequest);
        // Lấy tổng số trang
        int totalPages = productImagePages.getTotalPages();
        List<ProductImageResponse> imageResponses = productImagePages.getContent();
        return ResponseEntity.ok(ProductImageListResponse
                .builder()
                .productImages(imageResponses)
                .totalPages(totalPages)
                .build());
    }

}
