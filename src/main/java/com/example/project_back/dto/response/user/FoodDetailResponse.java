package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FoodDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;

    private String image;
    private List<String> images;


    private Double rating;
    private Integer reviewCount;
    private Integer soldCount;

    private String category;
}
