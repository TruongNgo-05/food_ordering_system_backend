package com.example.project_back.dto.response.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableAdminResponse {
    private Integer id;
    private String tableNumber;
    private String qrCode;
    private String status;
}
