package com.example.project_back.dto.request.spec;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewFoodParam {
    private String name;

    private Integer categoryId;
}
