package com.example.project_back.dto.response.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemAdminResponse {
    private Long foodId;

    private String foodName;

    private String image;

    private Double price;

    private Integer quantity;
}
