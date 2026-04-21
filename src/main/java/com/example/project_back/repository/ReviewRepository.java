package com.example.project_back.repository;

import com.example.project_back.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Optional<Review> findByFoodIdAndUserId(Long foodId, Long userId);

    List<Review> findByFoodId(Long foodId);
}
