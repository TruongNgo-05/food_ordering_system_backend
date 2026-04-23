package com.example.project_back.controller.customer;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.ReviewResponse;
import com.example.project_back.service.FoodService;
import com.example.project_back.service.ReviewService;
import com.example.project_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final ReviewService reviewService;
//    user
    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                userService.deleteUser(id),"delete susecfull"
        ));
    }
//    review
    @GetMapping("/review/{foodId}")
    public ResponseEntity<BaseResponse<Page<ReviewResponse>>> getAllcomment(
            @PathVariable Long foodId,@PageableDefault(size=10,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(new BaseResponse<>(
                reviewService.getReviewsByFood(foodId,pageable),
                "getAllCommentByFood sucess full "
        ));
    }
    @PostMapping("/review")
    public ResponseEntity<BaseResponse<ReviewResponse>> createReview(@RequestBody ReviewRequest reviewRequest){
        return ResponseEntity.ok(new BaseResponse<>(
                reviewService.createReview(reviewRequest),
                "createReview sucess full "
        ));
    }
    @PutMapping("/review/{id}")
    public ResponseEntity<BaseResponse<ReviewResponse>> updateReview(@PathVariable Long id, @RequestBody ReviewUpdateRequest update){
        return ResponseEntity.ok(new BaseResponse<>(
                reviewService.updateReview(id,update),
                "updateReview sucess full "
        ));
    }

    @DeleteMapping("/review/{id}")
    public ResponseEntity<BaseResponse<String>> deleteReview(@PathVariable Long id){
        return ResponseEntity.ok(new BaseResponse<>(
                reviewService.deleteReview(id),
                "DeleteReview sucess full "
        ));
    }
}
