package com.example.project_back.mapper;

import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.response.user.ReviewResponse;
import com.example.project_back.entity.Review;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class ReviewMapper {
    public static ReviewResponse toReviewDTO(Review review) {
        ReviewResponse dto = new ReviewResponse();
        BeanUtils.copyProperties(review, dto);
        if (review.getUser() != null) {
            dto.setUsername(review.getUser().getUsername());
        }
        return dto;
    }

    public static Review createReviewDto(ReviewRequest reviewRequest) {
        Review review = new Review();
        BeanUtils.copyProperties(reviewRequest, review);
        review.setCreatedAt(LocalDateTime.now());
        return  review;
    }

    public static void update(ReviewUpdateRequest reviewUpdateRequest , Review review){
       if(reviewUpdateRequest.getRating()!=null){
           review.setRating(reviewUpdateRequest.getRating());
       }
       if(reviewUpdateRequest.getComment()!=null){
           review.setComment(reviewUpdateRequest.getComment());
       }
    }
}
