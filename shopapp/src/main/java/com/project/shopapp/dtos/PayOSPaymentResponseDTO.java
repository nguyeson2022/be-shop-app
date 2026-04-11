package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayOSPaymentResponseDTO {
    private String bin;
    
    @JsonProperty("account_number")
    private String accountNumber;
    
    @JsonProperty("account_name")
    private String accountName;
    
    private Integer amount;
    private String description;
    
    @JsonProperty("order_code")
    private Long orderCode;
    
    private String currency;
    
    @JsonProperty("payment_link_id")
    private String paymentLinkId;
    
    private String status;
    
    @JsonProperty("checkout_url")
    private String checkoutUrl;
    
    @JsonProperty("qr_code")
    private String qrCode;
}