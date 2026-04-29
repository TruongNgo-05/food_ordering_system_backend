package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Integer id;
    private String address;
    private Boolean isDefault;
}
