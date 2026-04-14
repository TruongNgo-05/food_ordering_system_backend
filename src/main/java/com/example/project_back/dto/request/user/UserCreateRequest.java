package com.example.project_back.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    @NotNull(message = "Username not null")
    private String username;

    @NotNull(message = "FirstName not null")
    private String firstName;

    @NotNull(message = "LastName not null")
    private String lastName;

    @NotNull(message = "Email not null")
    private String email;

    @NotNull(message = "Password not null")
    private String password;

    @NotNull(message = "ConfirmPassword not null")
    private String ConfirmPassword;
}
