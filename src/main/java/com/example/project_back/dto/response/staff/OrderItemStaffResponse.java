package com.example.project_back.dto.response.staff;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemStaffResponse {
    private Long foodId;

    private String foodName;

    private String image;

    private Double price;

    private Integer quantity;
}
