package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.FoodOderTableResponse;
import com.example.project_back.dto.response.user.FoodResponse;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.FoodImage;
import com.example.project_back.entity.Review;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

//    admin
    public static FoodAdminResponse toMapperAdmin(Food food){
        FoodAdminResponse foodAdminResponse = new FoodAdminResponse();
        BeanUtils.copyProperties(food, foodAdminResponse);
        return foodAdminResponse;
    }

    public static FoodDetailAdminRespone toMapperAdminDetail(Food food){
        FoodDetailAdminRespone foodAdminDetail = new FoodDetailAdminRespone();
        BeanUtils.copyProperties(food, foodAdminDetail);
        foodAdminDetail.setImage(food.getImage());
        // images
        if(food.getImages() != null){
            foodAdminDetail.setImages(
                    FoodImageMapper.toUrlList(food.getImages())
            );
        }
        return foodAdminDetail;
    }

    public static FoodDetailResponse toMapperDetail(Food food){
        FoodDetailResponse dto = new FoodDetailResponse();
        BeanUtils.copyProperties(food, dto);
        if(food.getCategories() != null){
            dto.setCategory(food.getCategories().getName());
        }
        dto.setImage(food.getImage());
        // images
        if(food.getImages() != null){
            dto.setImages(
                    FoodImageMapper.toUrlList(food.getImages())
            );
        }
        //   REVIEW + RATING
        if(food.getReviews() != null && !food.getReviews().isEmpty()){

            dto.setReviewCount(food.getReviews().size());

            double avgRating = food.getReviews().stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            dto.setRating(avgRating);

        } else {
            dto.setReviewCount(0);
            dto.setRating(0.0);
        }

        return dto;
    }

    public static Food toCreate(FoodCreateAndUpdateRequest createDto){
        Food food = new Food();
        BeanUtils.copyProperties(createDto, food);
        food.setStatus(true);
        food.setRating(0.0);
        food.setSoldCount(0);
        food.setStatus(true);
        food.setCreatedAt(LocalDateTime.now());
        // images
        if(createDto.getImages() != null){
            food.setImages(
                    FoodImageMapper.toEntityList(createDto.getImages(), food)
            );
        }
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
        // images
        if(dto.getImages() != null){
            food.setImages(
                    FoodImageMapper.toEntityList(dto.getImages(), food)
            );
        }
    }
}
