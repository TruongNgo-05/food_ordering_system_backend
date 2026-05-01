package com.example.project_back.dto.response.customer.cart;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Integer cartId;
    private List<CartItemResponse> items;
    private Double totalPrice;
}