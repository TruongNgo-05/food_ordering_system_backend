package com.example.project_back.dto.response.admin;

import com.example.project_back.constant.OrderStatus;

import java.time.LocalDateTime;

public class OrderAdminResponse {
    private Long orderId;
    private String orderCode;
    private LocalDateTime orderDate;
    private Double orderPrice;
    private OrderStatus orderStatus;
}
