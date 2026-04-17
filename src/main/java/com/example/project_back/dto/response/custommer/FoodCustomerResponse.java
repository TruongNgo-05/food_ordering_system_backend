package com.example.project_back.dto.response.custommer;

import com.example.project_back.entity.Categories;

import java.time.LocalDateTime;

public class FoodCustomerResponse {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String image;

    private Double rating;

    private Integer soldCount;

    private Boolean status;

    private LocalDateTime createdAt;

    private Categories categories;
}
