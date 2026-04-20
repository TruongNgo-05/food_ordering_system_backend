package com.example.project_back.dto.request.admin;

import com.example.project_back.constant.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {

    private String email;

    private Role role;

}
