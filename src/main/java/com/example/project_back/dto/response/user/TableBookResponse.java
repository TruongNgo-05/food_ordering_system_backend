package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableBookResponse {
    private TableResponse tableDetail;
    private String status;
}
