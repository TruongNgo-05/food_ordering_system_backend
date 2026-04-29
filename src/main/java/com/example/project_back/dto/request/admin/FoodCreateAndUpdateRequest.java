package com.example.project_back.dto.request.admin;

import com.example.project_back.entity.Categories;
import com.example.project_back.entity.FoodImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class FoodCreateAndUpdateRequest {
    private String name;

    private String description;

    private Double price;

    private Boolean status;

    private Integer categoryId;

    private String imageUrl;

    private List<String> imageUrls;

    private Boolean removeImage;
}
