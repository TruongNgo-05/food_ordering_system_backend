package com.example.project_back.dto.request.user;

import com.example.project_back.constant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
    @NotNull(message = "Username not null")
    private String username;

    @NotNull(message = "fullName not null")
    private String fullName;

    @NotNull(message = "Phone not null")
    private String phone;

    @NotNull(message = "Email not null")
    private String email;

    @NotNull(message = "Password not null")
    private String passWord;

    @NotNull(message = "ConfirmPassword not null")
    private String confirmPassword;

    private Role role;
}
