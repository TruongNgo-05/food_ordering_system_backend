package com.example.project_back.dto.response.admin;

import com.example.project_back.entity.Categories;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"id","name","price","image","status","categories"})
public class FoodAdminResponse {

    private Long id;

    private String name;

    private Double price;

    private String image;

    private Boolean status;

    private String categoryName;
}
