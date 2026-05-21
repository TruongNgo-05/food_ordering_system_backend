package com.example.project_back.dto.response.customer.order;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentMethodType;
import com.example.project_back.dto.response.customer.order.reponseOrder.PaymentOrder;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@JsonPropertyOrder({"orderId","orderCode","status","paymentMethod","totalPrice","totalItems","createdAt","items"})
public class MyOrderResponse {
    private Long orderId;
    private String orderCode;
    private OrderStatus status;
    private PaymentOrder payment;
    private Double totalPrice;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
