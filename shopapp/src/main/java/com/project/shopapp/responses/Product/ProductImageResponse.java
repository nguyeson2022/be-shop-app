package com.project.shopapp.responses.Product;

import com.project.shopapp.models.ProductImage;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;
    private String image_url;
    private String productId;

    public static ProductImageResponse fromProductImage(ProductImage productImage){
        ProductImageResponse productImageResponse = ProductImageResponse
                .builder()
                .id(productImage.getId())
                .image_url(productImage.getImageUrl())
                .productId(productImage.getProduct().getName())
                .build();
        return productImageResponse;
    }
}
