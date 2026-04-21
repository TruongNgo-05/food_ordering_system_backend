package com.example.project_back.specification;

import com.example.project_back.entity.Categories;
import org.springframework.data.jpa.domain.Specification;

public class CategoriesSpecification {

    public static Specification<Categories> hasCategoriesName(String name){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("name")),"%"+name.toUpperCase()+"%");
        };
    }
}
