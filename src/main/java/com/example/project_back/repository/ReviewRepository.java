package com.example.project_back.repository;

import com.example.project_back.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Optional<Review> findByFoodIdAndUserId(Long foodId, Long userId);

    Page<Review> findByFoodId(Long foodId, Pageable pageable);

//    avg
@Query("SELECT AVG(r.rating) FROM Review r WHERE r.food.id = :foodId")
Double getAverageRatingByFoodId(@Param("foodId") Long foodId);

//so comment
@Query("SELECT COUNT(r) FROM Review r WHERE r.food.id = :foodId")
Long countByFoodId(@Param("foodId") Long foodId);
}
