package com.project.shopapp.models;

public enum OrderStatus {
    PENDING,      // Chờ xử lý
    PROCESSING,   // Đang xử lý
    SHIPPED,      // Đã giao hàng
    DELIVERED,    // Đã nhận hàng
    COMPLETED,    // Đã hoàn thành
    CANCELLED,     // Đã hủy
    FAILED,        // Thất bại
    PAID          // Đã thanh toán
}
