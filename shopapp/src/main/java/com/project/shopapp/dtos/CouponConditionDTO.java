package com.project.shopapp.dtos;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponConditionDTO {
    private Long id;
    private String attribute;
    private String operator;
    private String value;
    private BigDecimal discountAmount;
}

