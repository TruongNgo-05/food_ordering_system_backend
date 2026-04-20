package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import com.example.project_back.entity.Food;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class FoodMapper {
    public static FoodResponse toMapperCustomer(Food food){
        FoodResponse foodResponse = new FoodResponse();
        BeanUtils.copyProperties(food, foodResponse);
        foodResponse.setCategoryId(food.getCategories().getId());
        return foodResponse;
    }

//    admin
    public static FoodAdminResponse toMapperAdmin(Food food){
        FoodAdminResponse foodAdminResponse = new FoodAdminResponse();
        BeanUtils.copyProperties(food, foodAdminResponse);
        return foodAdminResponse;
    }

    public static Food toCreate(FoodCreateAndUpdateRequest  foodCreateAndUpdateRequest){
        Food food = new Food();
        BeanUtils.copyProperties(foodCreateAndUpdateRequest, food);
        food.setStatus(true);
        food.setRating(0.0);
        food.setSoldCount(0);
        food.setStatus(true);
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
        if(dto.getImage() != null){
            food.setImage(dto.getImage());
        }
        if(dto.getStatus()!= null){
            food.setStatus(dto.getStatus());
        }
    }
}
