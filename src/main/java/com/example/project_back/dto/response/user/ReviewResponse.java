package com.example.project_back.dto.response.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({"id","username","rating","comment","createdAt","updatedAt"})
public class ReviewResponse {
    private Long id;
    private String username;
    private Double rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
