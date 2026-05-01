package com.example.project_back.dto.request.customer.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Long foodId;
    private Integer quantity;
}
