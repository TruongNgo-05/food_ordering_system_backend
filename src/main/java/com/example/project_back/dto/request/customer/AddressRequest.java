package com.example.project_back.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequest {

    private String receiverName;

    private String receiverPhone;

    @NotBlank(message = "Address must not be empty")
    private String address;

    private Boolean isDefault;
}
