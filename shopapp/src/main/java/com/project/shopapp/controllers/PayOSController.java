package com.project.shopapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.shopapp.dtos.PayOSPaymentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.model.webhooks.ConfirmWebhookResponse;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/payments/payos")
@RequiredArgsConstructor
@Slf4j
public class PayOSController {

    private final PayOS payOS;

    @PostMapping("/create-payment-link")
    public ObjectNode createPaymentLink(@Valid @RequestBody PayOSPaymentRequestDTO requestDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            // Tạo order code nếu chưa có
            long orderCode;
            if (requestDTO.getOrderCode() == null) {
                String currentTimeString = String.valueOf(new Date().getTime());
                orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
            } else {
                orderCode = requestDTO.getOrderCode();
            }

            // Tạo item data (v2)
            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(requestDTO.getItems().get(0).getName())
                    .price(Long.valueOf(requestDTO.getItems().get(0).getPrice()))
                    .quantity(requestDTO.getItems().get(0).getQuantity())
                    .build();

            // Tạo payment data (v2)
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .description(requestDTO.getDescription())
                    .amount(Long.valueOf(requestDTO.getAmount()))
                    .items(List.of(item))
                    .returnUrl(requestDTO.getReturnUrl())
                    .cancelUrl(requestDTO.getCancelUrl())
                    .build();

            CreatePaymentLinkResponse data = payOS.paymentRequests().create(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            log.error("Lỗi tạo payment link PayOS: ", e);
            response.put("error", -1);
            response.put("message", "fail: " + e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @GetMapping("/payment-info/{orderCode}")
    public ObjectNode getPaymentInformation(@PathVariable Long orderCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLink order = payOS.paymentRequests().get(orderCode);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            log.error("Lỗi lấy thông tin payment: ", e);
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PutMapping("/cancel/{orderCode}")
    public ObjectNode cancelPayment(@PathVariable Long orderCode,
            @RequestParam(required = false) String reason) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLink order = payOS.paymentRequests().cancel(orderCode, reason);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            log.error("Lỗi hủy payment: ", e);
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping("/webhook")
    public ObjectNode handleWebhook(@RequestBody Map<String, Object> webhookData) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            log.info("Received PayOS Webhook: {}", webhookData);

            // Lấy các thông tin cần thiết từ webhook
            String code = (String) webhookData.get("code");
            String desc = (String) webhookData.get("desc");
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");

            if ("00".equals(code)) {
                // Thanh toán thành công
                Long orderCode = Long.valueOf(data.get("orderCode").toString());
                Integer amount = Integer.valueOf(data.get("amount").toString());
                String reference = (String) data.get("reference");

                log.info("Payment successful - OrderCode: {}, Amount: {}, Reference: {}",
                        orderCode, amount, reference);

                // Xử lý logic webhook tại đây:
                // - Cập nhật trạng thái đơn hàng trong database
                // - Gửi email xác nhận
                // - Trigger các process khác

                response.put("error", 0);
                response.put("message", "Webhook processed successfully");
                response.set("data", objectMapper.valueToTree(data));
            } else {
                // Thanh toán thất bại hoặc có lỗi
                log.warn("Payment failed or error - Code: {}, Description: {}", code, desc);

                response.put("error", 0);
                response.put("message", "Webhook received but payment failed");
                response.set("data", objectMapper.valueToTree(webhookData));
            }

            return response;
        } catch (Exception e) {
            log.error("Lỗi xử lý webhook PayOS: ", e);
            response.put("error", -1);
            response.put("message", "Webhook processing failed: " + e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping("/confirm-webhook")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            ConfirmWebhookResponse result = payOS.webhooks().confirm(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(result.getWebhookUrl()));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            log.error("Lỗi confirm webhook: ", e);
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping("/create-embedded-payment-link")
    public ObjectNode createEmbeddedPaymentLink(@Valid @RequestBody PayOSPaymentRequestDTO requestDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            // Tạo order code
            long orderCode = System.currentTimeMillis() / 1000;

            PaymentLinkItem itemData = PaymentLinkItem.builder()
                    .name(requestDTO.getItems().get(0).getName())
                    .quantity(requestDTO.getItems().get(0).getQuantity())
                    .price(Long.valueOf(requestDTO.getItems().get(0).getPrice()))
                    .build();

            // Tạo payment data (v2)
            CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount(Long.valueOf(requestDTO.getAmount()))
                    .description("ABC")
                    .returnUrl(requestDTO.getReturnUrl())
                    .cancelUrl(requestDTO.getCancelUrl())
                    .items(List.of(itemData))
                    .build();

            CreatePaymentLinkResponse result = payOS.paymentRequests().create(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(result));
            return response;
        } catch (Exception e) {
            log.error("Lỗi tạo embedded payment link PayOS: ", e);
            response.put("error", -1);
            response.put("message", "fail: " + e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}