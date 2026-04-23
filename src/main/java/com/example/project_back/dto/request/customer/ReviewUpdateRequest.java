package com.example.project_back.dto.request.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateRequest {
    private Double rating;
    private String comment;
}
