package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.FoodOderTableResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import com.example.project_back.entity.Food;

import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;


public class FoodMapper {
    //user
    public static FoodResponse toMapperCustomer(Food food){
        FoodResponse foodResponse = new FoodResponse();
        BeanUtils.copyProperties(food, foodResponse);
        foodResponse.setCategoryId(food.getCategories().getId());
        return foodResponse;
    }

    public static FoodOderTableResponse toMapTable(Food food){
        FoodOderTableResponse dto = new FoodOderTableResponse();
        BeanUtils.copyProperties(food, dto);
        return dto;
    }
    public static FoodDetailResponse toMapperDetail(Food food){
        FoodDetailResponse dto = new FoodDetailResponse();
        BeanUtils.copyProperties(food, dto);
        return dto;
    }

//    admin
    public static FoodAdminResponse toMapperAdmin(Food food){
        FoodAdminResponse foodAdminResponse = new FoodAdminResponse();
        BeanUtils.copyProperties(food, foodAdminResponse);
        if (food.getCategories() != null) {
            foodAdminResponse.setCategoryName(food.getCategories().getName());
        }
        return foodAdminResponse;
    }

    public static FoodDetailAdminRespone toMapperAdminDetail(Food food){
        FoodDetailAdminRespone foodAdminDetail = new FoodDetailAdminRespone();
        BeanUtils.copyProperties(food, foodAdminDetail);
        foodAdminDetail.setCategoryName(food.getCategories().getName());
        foodAdminDetail.setImage(food.getImage());
        return foodAdminDetail;
    }


    public static Food toCreate(FoodCreateAndUpdateRequest createDto){
        Food food = new Food();

        food.setName(createDto.getName());
        food.setDescription(createDto.getDescription());
        food.setPrice(createDto.getPrice());
        food.setStatus(true);
        food.setRating(0.0);
        food.setSoldCount(0);
        food.setCreatedAt(LocalDateTime.now());

        return food;
    }

    public static void toUpdate(FoodCreateAndUpdateRequest  dto, Food food){
        if(dto.getName() != null){
            food.setName(dto.getName());
        }
        if(dto.getDescription() != null){
            food.setDescription(dto.getDescription());
        }
        if(dto.getPrice() != null){
            food.setPrice(dto.getPrice());
        }
//        if(dto.getImage() != null){
////            food.setImage(dto.getImage());
//        }
        if(dto.getStatus()!= null){
            food.setStatus(dto.getStatus());
        }
    }
}
