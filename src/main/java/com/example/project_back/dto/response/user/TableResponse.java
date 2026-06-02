package com.example.project_back.dto.response.user;

import com.example.project_back.constant.TableStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableResponse {
    private String tableNumber;
    private String qrCode;
}
