package com.example.project_back.service;

import com.example.project_back.dto.response.customer.FavoriteResponse;
import com.example.project_back.dto.response.user.FoodResponse;

import java.util.List;

public interface FavoriteService {
    FavoriteResponse getMyFavorite();

    String toggleFavorite(Long foodId);
}
