package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QRCodeResponse {
    private Integer tableId;

    private String tableNumber;

    private String qrCodeUrl;
}
