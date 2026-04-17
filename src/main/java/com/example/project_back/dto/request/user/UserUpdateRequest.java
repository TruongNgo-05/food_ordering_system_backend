package com.example.project_back.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String avatar;

    private String fullName;

    private String phone;

    private String email;
}
