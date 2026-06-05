package com.example.project_back.dto.response.staff;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class OrderStaffOffLineResponse {
    private Long orderId;

    private String orderCode;

    private String customerName;

    private String customerPhone;

    private String tableNumber;

    private Double totalPrice;

    private OrderStatus status;

    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime createdAt;
}
