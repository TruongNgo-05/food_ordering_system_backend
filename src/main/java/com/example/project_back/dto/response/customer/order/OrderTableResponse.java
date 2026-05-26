package com.example.project_back.dto.response.customer.order;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderTableResponse {
    private Long orderId;
    private String orderCode;

    private String tableNumber;
    private String customerName;
    private String customerPhone;

    private Double totalPrice;

    private String paymentUrl;

    private OrderStatus status;

    private List<OrderItemResponse> items;
}
