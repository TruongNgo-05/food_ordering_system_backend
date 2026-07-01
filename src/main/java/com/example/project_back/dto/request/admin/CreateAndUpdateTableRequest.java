package com.example.project_back.dto.request.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAndUpdateTableRequest {
    private String tableNumber;
    private Integer capacity;
}
