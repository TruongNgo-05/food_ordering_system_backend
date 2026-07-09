package com.example.project_back.dto.response.user;

import com.example.project_back.constant.SupportStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

public class SupportResponse {

    private Integer id;

    private Long userId;

    private String fullName;

    private String email;

    private String supportCode;

    private String subject;

    private String message;

    private String reply;

    private SupportStatus status;

    private LocalDateTime createdAt;
}
