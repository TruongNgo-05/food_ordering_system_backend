package com.example.project_back.dto.response.staff;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class OrderDetailStaffResponse {
    private Long orderId;

    private String orderCode;

    private String customerName;

    private String customerPhone;

    private String address;

    private String note;

    private Double totalPrice;

    private Double discount;

    private OrderStatus status;

    private String paymentMethod;

    private String paymentStatus;

    private LocalDateTime createdAt;

    private List<OrderItemStaffResponse> items;

    private String tableNumber;
}
