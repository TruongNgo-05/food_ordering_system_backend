package com.example.project_back.mapper;

import com.example.project_back.entity.Food;
import com.example.project_back.entity.FoodImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodImageMapper {
    // String -> Entity
    public static FoodImage toEntity(String url, Food food){
        FoodImage img = new FoodImage();
        img.setImageUrl(url);
        img.setFood(food);
        return img;
    }
    // List<String> -> List<Entity>
    public static List<FoodImage> toEntityList(List<String> urls, Food food) {

        List<FoodImage> result = new ArrayList<>();

        if (urls == null || urls.isEmpty()) {
            return result;
        }

        for (String url : urls) {
            result.add(toEntity(url, food));
        }

        return result;
    }

    // Entity -> String
    public static String toUrl(FoodImage img) {
        return img.getImageUrl();
    }

    // List<Entity> -> List<String>
    public static List<String> toUrlList(List<FoodImage> images) {
        List<String> result = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return result;
        }
        for (FoodImage img : images) {
            result.add(img.getImageUrl());
        }
        return result;
    }
}
