package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodTableResponse {
    private Long id;
    private String image;
    private String name;
    private Double price;
    private Integer categoryId;
}
