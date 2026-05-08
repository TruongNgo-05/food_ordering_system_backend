package com.example.project_back.dto.response.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class MyOrderResponse {
    private Integer orderId;
    private String orderCode;
    private String status;
    private String paymentMethod;
    private Double totalPrice;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
