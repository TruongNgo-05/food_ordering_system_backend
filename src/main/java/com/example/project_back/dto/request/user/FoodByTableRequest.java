package com.example.project_back.dto.request.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class FoodByTableRequest {
    private Long id;
    private String fullName;
    private String phone;
    private Long tableId;
}
