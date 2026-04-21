package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.response.user.ReviewResponse;
import com.example.project_back.entity.Food;
import com.example.project_back.entity.Review;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.mapper.ReviewMapper;
import com.example.project_back.repository.FoodRepository;
import com.example.project_back.repository.ReviewRepository;
import com.example.project_back.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements com.example.project_back.service.ReviewService {

    private final ReviewRepository reviewRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;


    // ================= CREATE =================
    @Transactional
    @Override
    public ReviewResponse createReview(ReviewRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User không tồn tại"));

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new ApplicationException("Food không tồn tại"));

        // ❗ mỗi user chỉ review 1 lần / 1 món
        if (reviewRepository.findByFoodIdAndUserId(food.getId(), user.getId()).isPresent()) {
            throw new ApplicationException("Bạn đã review món này rồi");
        }

        Review review = new Review();
        review.setUser(user);
        review.setFood(food);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return ReviewMapper.toReviewDTO(reviewRepository.save(review));
    }
//    / ================= UPDATE =================
@Transactional
@Override
public ReviewResponse updateReview(Long id, ReviewRequest request) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy review"));

        // ❗ chỉ sửa review của mình
        if (!review.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Bạn không có quyền sửa review này");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        review.setUpdatedAt(LocalDateTime.now());

        return ReviewMapper.toReviewDTO(reviewRepository.save(review));
    }

    // ================= DELETE =================
    @Transactional
    @Override
    public String deleteReview(Long id) {

        String username = SecurityUtils.getCurrentUsername();

        if (username == null || username.equals("anonymousUser")) {
            throw new ApplicationException("Bạn chưa đăng nhập");
        }

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy review"));

        // ❗ chỉ xoá review của mình
        if (!review.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Bạn không có quyền xoá review này");
        }

        reviewRepository.delete(review);

        return "Deleted successfully";
    }

    // ================= GET BY FOOD =================
    @Override
    public List<ReviewResponse> getReviewsByFood(Long foodId) {

        // check food tồn tại (optional nhưng nên có)
        if (!foodRepository.existsById(foodId)) {
            throw new ApplicationException("Food không tồn tại");
        }

        return reviewRepository.findByFoodId(foodId)
                .stream()
                .map(ReviewMapper::toReviewDTO)
                .toList();
    }

}
