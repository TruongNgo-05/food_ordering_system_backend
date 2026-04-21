package com.example.project_back.mapper;

import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.entity.Categories;
import org.springframework.beans.BeanUtils;

public class CategoriesMapper {
    public static CategoriesResponse toResponse(Categories categories) {
        CategoriesResponse response = new CategoriesResponse();
        BeanUtils.copyProperties(categories, response);
        return response;
    }

    public static Categories toEntity(CategoriesCreateAndUpdate create){
        Categories categories = new Categories();
        BeanUtils.copyProperties(create, categories);
        return categories;
    }

    public static void updateEntity (CategoriesCreateAndUpdate update , Categories categories){
        if(update.getName() != null && !update.getName().isEmpty()){
            categories.setName(update.getName());
        }
    }
}
