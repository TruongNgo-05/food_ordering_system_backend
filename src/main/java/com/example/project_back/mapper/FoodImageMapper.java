package com.example.project_back.mapper;

import com.example.project_back.entity.Food;
import com.example.project_back.entity.FoodImage;

import java.util.List;
import java.util.stream.Collectors;

public class FoodImageMapper {

    public static FoodImage toEntity(String url, Food food){
        FoodImage img = new FoodImage();
        img.setImageUrl(url);
        img.setFood(food);
        return img;
    }
    // List<String> -> List<Entity>
    public static List<FoodImage> toEntityList(List<String> urls, Food food){
        return urls.stream()
                .map(url -> toEntity(url, food))
                .collect(Collectors.toList());
    }

    // Entity -> String
    public static String toUrl(FoodImage img){
        return img.getImageUrl();
    }

    // List<Entity> -> List<String>
    public static List<String> toUrlList(List<FoodImage> images){
        return images.stream()
                .map(FoodImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
