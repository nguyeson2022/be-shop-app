package com.project.shopapp.services.image;

import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.responses.Product.ProductImageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductImageService implements IProductImageService{
    private final ProductImageRepository productImageRepository;
    @Override
    @Transactional
    public ProductImage deleteProductImage(Long id) throws Exception {
        Optional<ProductImage> productImage = productImageRepository.findById(id);
        if(productImage.isEmpty()) {
            throw new DataNotFoundException(
                    String.format("Cannot find product image with id: %ld", id)
            );
        }
        productImageRepository.deleteById(id);
        return productImage.get();
    }

    @Override
    public Page<ProductImageResponse> getAllProductImages(Long productId, PageRequest pageRequest) {
        Page<ProductImage> productImages;
        productImages = productImageRepository.searchProductImages(productId, pageRequest);
        return productImages.map(ProductImageResponse::fromProductImage);
    }

    @Override
    public ProductImage updateProductImage(Long id, ProductImageDTO productImageDTO) throws Exception {
        return null;
    }
}
