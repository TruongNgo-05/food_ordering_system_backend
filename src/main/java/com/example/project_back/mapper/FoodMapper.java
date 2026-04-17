package com.example.project_back.mapper;

import com.example.project_back.dto.response.custommer.FoodCustomerResponse;
import com.example.project_back.entity.Food;
import org.springframework.beans.BeanUtils;

public class FoodMapper {
    public static FoodCustomerResponse map(Food food){
        FoodCustomerResponse foodCustomerResponse = new FoodCustomerResponse();
        BeanUtils.copyProperties(food, foodCustomerResponse);
        return foodCustomerResponse;
    }
}
