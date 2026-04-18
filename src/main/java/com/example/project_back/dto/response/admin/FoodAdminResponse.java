package com.example.project_back.dto.response.admin;

import com.example.project_back.entity.Categories;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonPropertyOrder({"id","name","description","price","image","rating","soldCount","status","createdAt","categories"})
public class FoodAdminResponse {

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
