package com.example.project_back.dto.response.customer.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long foodId;
    private String foodName;
    private String image;
    private Double price;
    private Integer quantity;
}
