package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {
    private Integer id;

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    private String role;
}
