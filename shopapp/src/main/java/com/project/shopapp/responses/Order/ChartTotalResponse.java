package com.project.shopapp.responses.Order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChartTotalResponse {
    private int month;
    private double totalMoney;
}
