package com.example.project_back.dto.response.user;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;

    private String email;

    private String username;

    private String fullName;

    private Role role;

    private Status status;

    private LocalDateTime createdDate;

    private Integer failCount;

    private LocalDateTime lockTime;

}
