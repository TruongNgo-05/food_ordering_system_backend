package com.example.project_back.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({"id","title","description","imageUrl","isActive"})
public class BannerAdminResponse {
    private Integer id;

    private String title;

    private String description;

    private String imageUrl;

    private Boolean isActive;
}
