package com.example.project_back.dto.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateAndUpdateRequest {
    @NotNull
    private String title;

    private String description;

    private String imageUrl;

    private Boolean isActive;
}
