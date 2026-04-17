package com.example.project_back.dto.response.user;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({"id","avatar","email","username","fullName","phone","role","status","createdDate","failCount","lonkTime"})
public class UserResponse {
    private Long id;

    private String avatar;

    private String email;

    private String username;

    private String fullName;

    private String phone;

    private Role role;

    private Status status;

    private LocalDateTime createdDate;

    private Integer failCount;

    private LocalDateTime lockTime;

}
