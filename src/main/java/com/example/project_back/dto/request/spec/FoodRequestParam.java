package com.example.project_back.dto.request.spec;

import com.example.project_back.entity.Categories;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FoodRequestParam {

    private String name;

    private Double minPrice;

    private Double maxPrice;

    private Double minRating;

    private Double maxRating;

    private Integer soldCount;

    private String categories;
}
