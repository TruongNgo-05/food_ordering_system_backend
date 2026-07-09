package com.example.project_back.service;

import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {
    Page<FoodResponse> getAllFoodCustomer(FoodRequestParam param, Pageable pageable);

    Page<FoodAdminResponse> getAllFoodAdmin(FoodRequestParam param, Pageable pageable);

    FoodDetailAdminRespone getById(Long id);

    FoodDetailResponse getFoodDetail(Long id);

    FoodAdminResponse createFood(
        FoodCreateAndUpdateRequest create,
        MultipartFile image,
        List<MultipartFile> images
    );

    FoodAdminResponse updateFood(
        Long id,
        FoodCreateAndUpdateRequest update,
        MultipartFile image,
        List<MultipartFile> images
    );

    String deleteFood(Long id);

    String deleteMainImage(Long id);

    String deleteSubImage(Long imageId);
}
