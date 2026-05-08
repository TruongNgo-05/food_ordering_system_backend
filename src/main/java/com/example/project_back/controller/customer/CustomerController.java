package com.example.project_back.controller.customer;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.customer.AddressRequest;
import com.example.project_back.dto.request.customer.ReviewRequest;
import com.example.project_back.dto.request.customer.ReviewUpdateRequest;
import com.example.project_back.dto.request.customer.cart.AddToCartRequest;
import com.example.project_back.dto.request.customer.cart.UpdateCartRequest;
import com.example.project_back.dto.request.customer.order.CreateOrderRequest;
import com.example.project_back.dto.response.customer.FavoriteResponse;
import com.example.project_back.dto.response.customer.order.CreateOrderResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherGetResponse;
import com.example.project_back.dto.response.customer.cart.CartResponse;
import com.example.project_back.dto.response.customer.voucher.VoucherResponse;
import com.example.project_back.dto.response.user.AddressResponse;
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
    private final VoucherService voucherService;
    private final CartService cartService;
    private final FavoriteService favoriteService;
    private final OrderService orderService;


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
                userService.getMyAddresses(),
                "getMyAddress sucess full "
        ));
    }

    @PostMapping("/address")
    public ResponseEntity<BaseResponse<AddressResponse>> createAddress(@RequestBody AddressRequest request){
        return ResponseEntity.ok(new BaseResponse<>(
                userService.createAddress(request),
                "createAddress sucess full "
        ));
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<BaseResponse<AddressResponse>> updateRequest(@PathVariable Integer id ,@RequestBody AddressRequest request){
        return ResponseEntity.ok(new BaseResponse<>(
                userService.updateAddress(id,request),
                "update sucess full "
        ));
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<BaseResponse<String>> deleteAddress(@PathVariable Integer id){
        return ResponseEntity.ok(new BaseResponse<>(
                userService.deleteAddress(id),
                "DeleteReview sucess full "
        ));
    }


    //    voucher
    @GetMapping("/voucher")
    public ResponseEntity<BaseResponse<List<VoucherGetResponse>>> getVoucher() {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVoucherCustomer(),
                "success"
        ));
    }

    @GetMapping("/check-voucher")
    public ResponseEntity<BaseResponse<VoucherResponse>> checkVoucher(@RequestParam String code){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.checkVoucherCode(code),
                "success"
        ));
    }

    @PostMapping ("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request ) {
        return ResponseEntity.ok( orderService.createOrder(request) );
    }

    @GetMapping("order/my-orders")
    public ResponseEntity<?> myOrders() {
        return ResponseEntity.ok( orderService.getMyOrders() );
    }

    @GetMapping("order/{id}")
    public ResponseEntity<?> orderDetail( @PathVariable Integer id ) {
        return ResponseEntity.ok( orderService.getOrderDetail(id) );
    }

    @PutMapping("order/{id}/cancel")
    public ResponseEntity<?> cancelOrder( @PathVariable Integer id ) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("Cancel success");
    }

    @PostMapping("order/{id}/reorder")
    public ResponseEntity<?> reorder( @PathVariable Integer id ) {
        orderService.reorder(id);
        return ResponseEntity.ok("Reorder success");
    }

// total pice
//    @PostMapping("/check-discount")
//    public ResponseEntity<BaseResponse<OrderCheckResponse>> checkDiscount(@RequestParam String code) {
//        return ResponseEntity.ok(new BaseResponse<>(
//                OrderService.checkDiscount(code),
//                "success"
//        ));
//    }
//    @PostMapping("/order/apply")
//    public ResponseEntity<BaseResponse<OrderCheckResponse>> orderApply(@RequestParam String code) {
//        return ResponseEntity.ok(new BaseResponse<>(
//                voucherService.OrderService(code),
//                "success"
//        ));
//    }

    //favorite
    @GetMapping("/favorites")
    public ResponseEntity<BaseResponse<FavoriteResponse>> getMyFavorites() {
        return ResponseEntity.ok( new BaseResponse<>(
                favoriteService.getMyFavorite(),
                "hiển thị yêu thich thanh cong"
        ));
    }

    @PostMapping("/favorites/toggle/{foodId}")
    public ResponseEntity<BaseResponse<String>> toggleFavorite(@PathVariable Long foodId) {
        return ResponseEntity.ok( new BaseResponse<>(
                favoriteService.toggleFavorite(foodId),
                "ccap nhat yêu thich thanh cong"
        ));
    }
}
