package com.example.project_back.dto.response.customer.order;

import com.example.project_back.dto.response.customer.cart.CartItemResponse;
import com.example.project_back.entity.CartItem;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailResponse {
    private OrderResponse order;
    private List<CartItemResponse> items;
    private Integer addressId;
    private Integer voucherId;

    private Double discount;        // số tiền giảm thực tế
     private Double totalPrice;     // tổng tiền giỏ hàng
}
