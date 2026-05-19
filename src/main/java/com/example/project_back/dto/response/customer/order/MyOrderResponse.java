package com.example.project_back.dto.response.customer.order;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@JsonPropertyOrder({"orderId","orderCode","status","paymentMethod","totalPrice","totalItems","createdAt","items"})
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
