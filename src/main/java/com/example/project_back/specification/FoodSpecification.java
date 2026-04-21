package com.example.project_back.specification;

import com.example.project_back.entity.Categories;
import com.example.project_back.entity.Food;
import org.springframework.data.jpa.domain.Specification;
public class FoodSpecification {
    public static Specification<Food> hasName(String name) {
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        });
    }

    public static Specification<Food> hasPrice(Double minPrice, Double maxPrice) {
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("price"),minPrice,maxPrice);
        });
    }

    public static Specification<Food> hasRating(Double minRating, Double maxRating) {
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
        });
    }
    public static Specification<Food> hasStatus(Boolean status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Food> hasCategoryId(Integer categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("categories").get("id"), categoryId);
    }


}
