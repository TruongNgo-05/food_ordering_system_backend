package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableBookResponse {
    private Integer tableId;
    private String tableNumber;
    private Integer capacity;
}
