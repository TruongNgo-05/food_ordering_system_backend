package com.example.project_back.service;

import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FoodService {
    Page<FoodResponse> getAllFoodCustomer(FoodRequestParam param, Pageable pageable);

    Page<FoodAdminResponse> getAllFoodAdmin(FoodRequestParam param, Pageable pageable);

    FoodDetailAdminRespone getById(Long id);

    FoodDetailResponse getFoodDetail(Long id);

    FoodAdminResponse createFood(FoodCreateAndUpdateRequest create);

    FoodAdminResponse updateFood(FoodCreateAndUpdateRequest update, Long id);

    String deleteFood(Long id);
}
