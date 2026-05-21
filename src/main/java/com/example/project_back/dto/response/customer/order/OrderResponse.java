package com.example.project_back.dto.response.customer.order;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private Long orderId;

    private String orderCode;
    private Double priceBefore;
    private Double totalPrice;

    private String paymentUrl;

    private OrderStatus status;
}
