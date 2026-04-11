package com.project.shopapp.services.image;

import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.Product.ProductImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductImageService {
    ProductImage deleteProductImage(Long id) throws Exception;

    Page<ProductImageResponse> getAllProductImages(Long productId, PageRequest pageRequest);

    ProductImage updateProductImage(Long id, ProductImageDTO productImageDTO) throws Exception;
}
