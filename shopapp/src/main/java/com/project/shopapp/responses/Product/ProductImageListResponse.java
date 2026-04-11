package com.project.shopapp.responses.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ProductImageListResponse {
    private List<ProductImageResponse> productImages;
    private int totalPages;
}
