package com.example.project_back.dto.request.admin;

import com.example.project_back.entity.Categories;
import com.example.project_back.entity.FoodImage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodCreateAndUpdateRequest {
    private String name;

    private String description;

    private Double price;

    private String image;

    private List<String> images;

    private Boolean status;

    private Integer categoryId;
}
