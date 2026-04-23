package com.example.project_back.dto.response.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@JsonPropertyOrder({"id","name","description","price","image","images","rating","reviewCount","soldCount","category"})
public class FoodDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;

    private String image;
    private List<String> images;


    private Double rating;
    private Long reviewCount;
    private Integer soldCount;

    private String categoryName;
}
