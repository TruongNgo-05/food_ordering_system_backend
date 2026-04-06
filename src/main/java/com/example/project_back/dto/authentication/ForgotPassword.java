package com.example.project_back.dto.authentication;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPassword {
    @NotEmpty(message = "email not empty")
    private String email;
}
