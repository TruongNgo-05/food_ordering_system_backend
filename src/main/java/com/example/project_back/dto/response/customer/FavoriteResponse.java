package com.example.project_back.dto.response.customer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FavoriteResponse {
    private List<Long> favoriteIds;
}