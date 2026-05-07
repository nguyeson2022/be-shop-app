package com.project.shopapp.dtos;

import com.project.shopapp.responses.Product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String response;
    private LocalDateTime responseTime;
    private List<ProductResponse> products = new ArrayList<>();

    public ChatResponse(String response, LocalDateTime responseTime) {
        this.response = response;
        this.responseTime = responseTime;
        this.products = new ArrayList<>();
    }
}
