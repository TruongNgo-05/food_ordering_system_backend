package com.example.project_back.controller.customer;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.request.customer.cart.AddToCartRequest;
import com.example.project_back.dto.request.customer.cart.UpdateCartRequest;
import com.example.project_back.dto.response.customer.VoucherResponse;
import com.example.project_back.dto.response.customer.cart.CartResponse;
import com.example.project_back.dto.response.user.AddressResponse;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.dto.response.user.ReviewResponse;
import com.example.project_back.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final UserAddressService userAddressService;
    private final VoucherService voucherService;
    private final CartService cartService;

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
    // cart

    @GetMapping("cart")
    public ResponseEntity<BaseResponse<CartResponse>> getCart() {
        return ResponseEntity.ok(new BaseResponse<>(
                cartService.getCart(),
                "getCartSucess full "
        ));
    }

    @PostMapping("cart")
    public ResponseEntity<BaseResponse<CartResponse>> addToCart(@RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(
                cartService.addToCart(request),
                "thêm món ăn vào giỏ success full "
        ));
    }

    @PutMapping("cart/{itemId}")
    public ResponseEntity<BaseResponse<CartResponse>> updateCart(@PathVariable Integer itemId, @RequestBody UpdateCartRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(
                cartService.updateCartItem(itemId, request),
                "updateCartItem sucess full "
        ));
    }

    @DeleteMapping("cart/{itemId}")
    public ResponseEntity<BaseResponse<CartResponse>> deleteCart(@PathVariable Integer itemId) {
        return ResponseEntity.ok(new BaseResponse<>(
                cartService.removeCartItem(itemId),
                "deleteCartItem sucess full "
                ));
    }



//    address
    @GetMapping("/address")
    public ResponseEntity<BaseResponse<List<AddressResponse>>> getMyAddress(){
        return ResponseEntity.ok(new BaseResponse<>(
                userAddressService.getMyAddresses(),
                "getMyAddress sucess full "
        ));
    }
    @PostMapping("/address")
    public ResponseEntity<BaseResponse<AddressResponse>> createAddress(@RequestBody AddressRequest request){
        return ResponseEntity.ok(new BaseResponse<>(
                userAddressService.createAddress(request),
                "createAddress sucess full "
        ));
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<BaseResponse<AddressResponse>> updateRequest(@PathVariable Integer id ,@RequestBody AddressRequest request){
        return ResponseEntity.ok(new BaseResponse<>(
                userAddressService.updateAddress(id,request),
                "update sucess full "
        ));
    }
    @DeleteMapping("/address/{id}")
    public ResponseEntity<BaseResponse<String>> deleteAddress(@PathVariable Integer id){
        return ResponseEntity.ok(new BaseResponse<>(
                userAddressService.deleteAddress(id),
                "DeleteReview sucess full "
        ));
    }
//    voucher
    @PostMapping("/voucher/apply")
    public ResponseEntity<BaseResponse<VoucherResponse>> voucherApply(@RequestParam String code,
                                                                      @RequestParam Double total) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.usedVoucher(code, total),
                "success"
        ));
    }


}
