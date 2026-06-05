package com.example.project_back.dto.response.admin;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReviewFoodAdminResponse {
    private Long foodId;
    private String foodName;

    private String image;

    private Long reviewCount;
    private Double averageRating;

    private Integer categoryId;
}
