package com.example.project_back.dto.response.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class OrderResponse {
    private Long orderId;
    private String orderCode;
    private Double orderPrice;// tiền sau giảm
    private LocalDateTime orderDate;
    private Integer totalFood;
    private String orderStatus;
    private String paymentMethod;
}
