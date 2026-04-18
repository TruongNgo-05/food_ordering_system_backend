package com.example.project_back.specification;

import com.example.project_back.entity.Categories;
import org.springframework.data.jpa.domain.Specification;

public class CategoriesSpecification {

    public static Specification<Categories> hasCategoriesName(String name){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("name")),"%"+name.toUpperCase()+"%");
        };
    }

    public static Specification<Categories> hasCategoriesId(Integer id){
        return (root, query, cb) -> {
            return cb.equal(root.get("id"), id);
        };
    }
}
