package com.example.project_back.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportRequest {

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

}