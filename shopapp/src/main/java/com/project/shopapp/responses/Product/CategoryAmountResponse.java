package com.project.shopapp.responses.Product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAmountResponse {
    private String category;
    private Long amount;
}
