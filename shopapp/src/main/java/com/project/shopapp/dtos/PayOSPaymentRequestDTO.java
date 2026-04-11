package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayOSPaymentRequestDTO {
//    @NotNull
    private Long orderCode;

    /** Số tiền thanh toán (integer, VND) – required */
    @NotNull
//    @Positive
    private Integer amount;

    /** Mô tả thanh toán – required */
//    @NotBlank
    private String description;

    /** Thông tin người mua (optional) */
    private String buyerName;
    private String buyerCompanyName;
    private String buyerTaxCode;
    private String buyerAddress;

    /** Email người mua (optional) */
//    @Email
    private String buyerEmail;

    /** SĐT người mua (optional) */
    private String buyerPhone;

    /** Danh sách sản phẩm – optional theo ảnh, nhưng thường nên gửi */
//    @Size(min = 1, message = "items không được rỗng khi gửi danh sách sản phẩm")
//    private List<PayosItem> items;

    /** URL khi người dùng huỷ – required */
//    @NotBlank
    private String cancelUrl;

    /** URL khi thanh toán thành công – required */
//    @NotBlank
    private String returnUrl;

    /**
     * Thông tin hoá đơn (object – optional).
     * Để linh hoạt vì tài liệu PayOS có thể thay đổi các field bên trong,
     * ta để kiểu JsonNode (hoặc Map<String, Object>) để map đúng y payload.
     */
    private JsonNode invoice;

    /** Thời gian hết hạn link (Unix timestamp, int32) – required */
//    @NotNull
//    @Positive
    private Long expiredAt;

    /**
     * Chữ ký HMAC_SHA256 – required.
     * Bạn sẽ ký theo hướng dẫn PayOS (sort theo alphabet, v.v.) trước khi set vào đây.
     */
//    @NotBlank
    private String signature;

    
    private List<PayOSItemDTO> items;
    
    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PayOSItemDTO {
        private String name;
        private Integer quantity;
        private Integer price;
    }
}