package com.project.shopapp.configurations;

import com.project.shopapp.filters.JwtTokenFilter;
import com.project.shopapp.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig implements WebMvcConfigurer {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            // Cho phép truy cập công khai tới /uploads/**
                            .requestMatchers("/uploads/**").permitAll()

                            // Các quy tắc khác giữ nguyên
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)
                            ).permitAll()

                            .requestMatchers(GET, String.format("%s/roles**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/products", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/users/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT, String.format("%s/users/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/products/images/*", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/products**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/product_images/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/articles/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT, String.format("%s/articles/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("%s/articles/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER, Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/orders/get-orders-by-keyword", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(GET, String.format("%s/order_details/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/order_details/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET, String.format("%s/coupons/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/report/excel", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/payment/payment_infor", apiPrefix)).permitAll()
                            .requestMatchers(String.format("%s/chatbot/**", apiPrefix)).permitAll()
                            // PayOS endpoints
                            .requestMatchers(POST, String.format("%s/payments/payos/create-payment-link", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/payments/payos/create-embedded-payment-link", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/payments/payos/payment-info/**", apiPrefix)).permitAll()
                            .requestMatchers(PUT, String.format("%s/payments/payos/cancel/**", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/payments/payos/webhook", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/payments/payos/confirm-webhook", apiPrefix)).permitAll()
                            
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);

        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("http://localhost:4200"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });

        return http.build();
    }
}