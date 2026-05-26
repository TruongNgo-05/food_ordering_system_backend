package com.example.project_back.dto.response.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class MenuTableResponse {

    private TableResponse table;

    private List<FoodTableResponse> foods;
}
