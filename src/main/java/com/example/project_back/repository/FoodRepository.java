package com.example.project_back.repository;

import com.example.project_back.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food,Long>, JpaSpecificationExecutor<Food> {
    List<Food> findByStatus(Boolean status);
}
