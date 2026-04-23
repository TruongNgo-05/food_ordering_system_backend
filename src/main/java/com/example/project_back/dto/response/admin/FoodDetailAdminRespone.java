package com.example.project_back.dto.response.admin;

import com.example.project_back.entity.Categories;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@JsonPropertyOrder({"id","name","description","price","image","images","rating","soldCount","status","createdAt","categories"})
public class FoodDetailAdminRespone {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String image;

    private List<String> images;

    private Double rating;

    private Integer soldCount;

    private Boolean status;

    private LocalDateTime createdAt;

    private String categoryName;
}
