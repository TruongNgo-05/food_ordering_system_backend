package com.example.project_back.service;

import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.response.user.ReviewResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(Long id, ReviewUpdateRequest request);

    String deleteReview(Long id);

    Page<ReviewResponse> getReviewsByFood(Long foodId, Pageable pageable);
}
