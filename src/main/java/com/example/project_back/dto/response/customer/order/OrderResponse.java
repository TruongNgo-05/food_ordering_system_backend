package com.example.project_back.dto.response.customer.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private Integer orderId;

    private String orderCode;

    private Double totalPrice;

    private Double totalAfter;

    private String paymentUrl;

    private String status;
}
