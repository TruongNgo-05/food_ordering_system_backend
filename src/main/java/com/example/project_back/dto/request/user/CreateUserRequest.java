package com.example.project_back.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    @NotNull(message = "Username not null")
    private String Username;

    @NotNull(message = "FirstName not null")
    private String FirstName;

    @NotNull(message = "LastName not null")
    private String LastName;

    @NotNull(message = "Email not null")
    private String Email;

    @NotNull(message = "Password not null")
    private String Password;

    @NotNull(message = "ConfirmPassword not null")
    private String ConfirmPassword;
}
