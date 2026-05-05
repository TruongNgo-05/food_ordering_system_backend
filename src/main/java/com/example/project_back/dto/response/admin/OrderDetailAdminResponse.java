package com.example.project_back.dto.response.admin;

import com.example.project_back.dto.response.customer.cart.CartItemResponse;

import java.util.List;

public class OrderDetailAdminResponse {
    private OrderAdminResponse  orderAdminResponse;
    private List<CartItemResponse> items;
    private Integer addressId;
    private Integer voucherId;

    private Double discount;        // số tiền giảm thực tế
    private Double totalPrice;     // tổng tiền giỏ hàng
}
