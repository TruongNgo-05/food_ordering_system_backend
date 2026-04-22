package com.example.project_back.dto.response.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class FoodTableResponse {
    private Long id;
    private String fullName;
    private String phone;
    private Integer tableId;
    private LocalDateTime createdAt;
}
