package com.example.project_back.dto.request.spec;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequestParam {
    private String email;

    private String fullName;

    private Role role;

    private Status status;

    private LocalDate minDate;

    private LocalDate maxDate;
}
