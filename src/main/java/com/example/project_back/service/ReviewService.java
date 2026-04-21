package com.example.project_back.service;

import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.response.user.ReviewResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(Long id, ReviewRequest request);

    String deleteReview(Long id);

    List<ReviewResponse> getReviewsByFood(Long foodId);
}
