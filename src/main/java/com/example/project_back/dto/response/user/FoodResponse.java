package com.example.project_back.dto.response.user;

import com.example.project_back.entity.Categories;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({"id","name","description","price","image","rating","soldCount","categories"})
public class FoodResponse {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String image;

    private Double rating;

    private Integer soldCount;

    private Integer categoryId;
}
