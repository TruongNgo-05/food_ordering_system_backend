package com.example.project_back.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FAQAdminResponse {

    private Integer id;

    private String question;

    private String answer;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}