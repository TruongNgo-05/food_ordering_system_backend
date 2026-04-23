package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.response.user.ReviewResponse;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.Review;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.ReviewMapper;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.ReviewRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;


        @Transactional
        @Override
        public ReviewResponse createReview(ReviewRequest request) {

            String username = SecurityUtils.getCurrentUsername();

            if (username == null || username.equals("anonymousUser")) {
                throw new ApplicationException("Bạn chưa đăng nhập");
            }
            Optional<User> user = userRepository.findByUsername(username);
            if(user.isEmpty()) {
                throw new ApplicationException("User không tồn tại");
            }
            Optional<Food> food = foodRepository.findById(request.getFoodId());
            if(food.isEmpty()) {
                throw new ApplicationException("Food không tồn tại");
            }
            Food foodItem = food.get();
            User userItem = user.get();
           if( reviewRepository.findByFoodIdAndUserId(foodItem.getId(),userItem.getId()).isPresent()){
                throw new ApplicationException("Bạn đã review món này rồi");
            }
            Review review = ReviewMapper.createReviewDto(request);
            review.setFood(foodItem);
            review.setUser(userItem);
            Review savedReview = reviewRepository.save(review);
            return ReviewMapper.toReviewDTO(savedReview);
        }

@Transactional
@Override
public ReviewResponse updateReview(Long id, ReviewUpdateRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Optional<Review> review = reviewRepository.findById(id);
        if(review.isEmpty()) {
            throw new ApplicationException("Không tìm thấy review");
        }
        Review reviewItem = review.get();
        if (!reviewItem.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Bạn không thể sửa review này");
        }
        ReviewMapper.update(request,reviewItem);
        return ReviewMapper.toReviewDTO(reviewRepository.save(reviewItem));
    }

    @Transactional
    @Override
    public String deleteReview(Long id) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Optional<Review> review = reviewRepository.findById(id);
        if(review.isEmpty()) {
            throw  new ApplicationException("Không tìm thấy review");
        }
        Review reviewItem = review.get();
        //  chỉ xoá review của mình
        if (!reviewItem.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Bạn không thể xoá review này");
        }
        reviewRepository.delete(reviewItem);
        return "Deleted successfully";
    }


    @Override
    public Page<ReviewResponse> getReviewsByFood(Long foodId, Pageable pageable) {
        Optional<Food> food = foodRepository.findById(foodId);
        if(food.isEmpty()){
            throw new ApplicationException(" khong tim thay mon an");
        }
        return reviewRepository.findByFoodId(foodId,pageable).map(ReviewMapper::toReviewDTO);
    }

}
