package com.example.project_back.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    private String emailOrUsername;

    private String password;
}
