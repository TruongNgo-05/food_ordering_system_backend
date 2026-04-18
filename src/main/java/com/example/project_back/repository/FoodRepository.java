package com.example.project_back.repository;

import com.example.project_back.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FoodRepository extends JpaRepository<Food,Long>, JpaSpecificationExecutor<Food> {

}
