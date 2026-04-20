package com.example.project_back.dto.request.admin;

import com.example.project_back.entity.Categories;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodCreateAndUpdateRequest {
    private String name;

    private String description;

    private Double price;

    private String image;

    private Boolean status;

    private Integer categoryId;
}
