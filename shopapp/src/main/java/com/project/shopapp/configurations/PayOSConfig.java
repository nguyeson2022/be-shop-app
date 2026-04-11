package com.project.shopapp.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {
    
    @Value("${payment.payOS.clientId}")
    private String clientId;
    
    @Value("${payment.payOS.apiKey}")
    private String apiKey;
    
    @Value("${payment.payOS.checksumKey}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}