package com.example.project_back.dto.response.admin;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderAdminResponse {
    private Long orderId;

    private String orderCode;

    private String customerName;

    private Double totalPrice;

    private String paymentMethod;

    private String paymentStatus;

    private OrderStatus status;
}
