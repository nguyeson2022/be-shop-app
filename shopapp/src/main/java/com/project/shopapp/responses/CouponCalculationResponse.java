package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponCalculationResponse {
    @JsonProperty("result")
    private Double result;
}