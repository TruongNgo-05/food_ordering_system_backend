package com.example.project_back.repository;

import com.example.project_back.entity.FoodImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodImageRepository extends JpaRepository<FoodImage, Long> {

    void deleteByFoodId(Long foodId);
}
