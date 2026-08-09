package com.example.project_back.dto.authentication;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {

    @NotEmpty(message = "email not empty")
    private String email;

    @NotNull(message = "otp not null")
    private Integer otp;
}