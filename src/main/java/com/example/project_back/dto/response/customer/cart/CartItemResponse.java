package com.example.project_back.dto.response.customer.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {
    private Integer itemId;
    private Long foodId;
    private String foodName;
    private Double price;
    private Integer quantity;
    private String image;
}
