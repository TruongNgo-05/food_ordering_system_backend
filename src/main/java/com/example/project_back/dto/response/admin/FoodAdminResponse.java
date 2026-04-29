package com.example.project_back.dto.response.admin;

import com.example.project_back.entity.Categories;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"id","name","description","price","image","status","categoryId","categoryName"})
public class FoodAdminResponse {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String image;

    private Boolean status;

    private String categoryName;

    private Integer categoryId;

    private List<FoodImageResponse> images;
}
