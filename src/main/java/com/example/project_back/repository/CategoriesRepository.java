package com.example.project_back.repository;

import com.example.project_back.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoriesRepository extends JpaRepository<Categories,Integer> , JpaSpecificationExecutor<Categories> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);
}
