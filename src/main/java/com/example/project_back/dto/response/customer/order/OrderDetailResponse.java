package com.example.project_back.dto.response.customer.order;

import com.example.project_back.dto.response.customer.cart.CartItemResponse;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@JsonPropertyOrder({"orderId","orderCode","status","paymentMethod","paymentStatus","discount","createdAt","items","totalPrice","address","note"})
public class OrderDetailResponse {
    private Integer orderId;
    private String orderCode;
    private String address;
    private String status;
    private String paymentStatus;
    private Double totalPrice;
    private String paymentMethod;
    private Double discount;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
