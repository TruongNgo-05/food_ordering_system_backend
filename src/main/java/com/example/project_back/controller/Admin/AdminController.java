package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.constant.OrderStatus;
import com.example.project_back.dto.request.admin.*;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.request.spec.VoucherRequestParam;
import com.example.project_back.dto.response.admin.*;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.dto.response.user.TableResponse;
import com.example.project_back.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/admin/")
public class AdminController {

    private final VoucherService  voucherService;
    private final TableService tableService;
    private final CategoriesService categoriesService;
    private final BannerService bannerService;
    private final FoodService foodService ;
    private final OrderService orderService;

    //    voucher
    @GetMapping("voucher")
    public ResponseEntity<BaseResponse<Page<VoucherAdminResponse>>> getVouchers(
            VoucherRequestParam param,@PageableDefault(size = 5 , sort = "id",direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVouchers(param,pageable),
                "get All vouchers successfully"
        ));
    }

    @GetMapping("voucher/{voucherId}")
    public ResponseEntity<BaseResponse<VoucherAdminDetailResponse>> getVoucherById(@PathVariable Integer voucherId) {
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.getVoucherById(voucherId),
                "get id vouchers successfully"
        ));
    }

    @PostMapping("voucher")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> createVoucher(@RequestBody @Valid VoucherCreateAndUpdateRequest create ){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.createVoucher(create),
                "create vouchers successfully"
        ));
    }

    @PutMapping("voucher/{id}")
    public ResponseEntity<BaseResponse<VoucherAdminResponse>> updateVoucher(@PathVariable Integer id,@RequestBody @Valid VoucherCreateAndUpdateRequest update){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.updateVoucher(id,update),
                "update vouchers successfully"
        ));
    }

    @DeleteMapping("voucher/{id}")
    public ResponseEntity<BaseResponse<String>> deleteVoucher(@PathVariable Integer id){
        return ResponseEntity.ok(new BaseResponse<>(
                voucherService.deleteVoucher(id),
                "delete vouchers successfully"
        ));
    }


    //    table
    @PostMapping("/table")
    public ResponseEntity<BaseResponse<TableResponse>> createTable(@RequestBody CreateAndUpdateTableRequest create) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.createTable(create),
                "create table successfully!"
        ));
    }

    @PutMapping("/table/{id}")
    public ResponseEntity<BaseResponse<TableResponse>> updateTable(@RequestBody CreateAndUpdateTableRequest update, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.updateTable(update, id),
                "update table successfully!"
        ));
    }

    @DeleteMapping("/table/{id}")
    public ResponseEntity<BaseResponse<String>> deleteTable(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                tableService.deleteTable(id),
                "delete table successfully!"
        ));
    }


    // categories
    @PostMapping("categories")
    public ResponseEntity<BaseResponse<CategoriesResponse>> createCategories(@RequestBody CategoriesCreateAndUpdate create) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.createCategories(create),
                "Create Categories Admin successfully!"
        ));
    }

    @PutMapping("categories/{id}")
    public ResponseEntity<BaseResponse<CategoriesResponse>> updateCategories(@RequestBody CategoriesCreateAndUpdate update, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.updateCategories(update, id),
                "Update Categories Admin successfully!"
        ));
    }

    @DeleteMapping("categories{id}")
    public ResponseEntity<BaseResponse<String>> deleteCategories(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.deleteCategories(id),
                "Delete Categories Admin successfully!"
        ));
    }
    

    //    banner
    @GetMapping("banner")
    public ResponseEntity<BaseResponse<Page<BannerAdminResponse>>> getAllBannerAdmin(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.getAllBannerAdmin(pageable),
                "get All Banner Admin successfully!"
        ));
    }

    @PostMapping("banner")
    public ResponseEntity<BaseResponse<BannerAdminResponse>> createBanner(@RequestBody @Valid BannerCreateAndUpdateRequest createDto) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.createBanner(createDto),
                "Create Banner successfully!"
        ));
    }

    @PutMapping("banner/{id}")
    public ResponseEntity<BaseResponse<BannerAdminResponse>> updateBanner(@RequestBody @Valid BannerCreateAndUpdateRequest updateDto, @PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.updateBanner(updateDto, id),
                "Update Banner successfully!"
        ));
    }

    @DeleteMapping("banner/{id}")
    public ResponseEntity<BaseResponse<String>> deleteBanner(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.deleteBanner(id),
                "Delete Banner successfully!"
        ));
    }


//    food
@GetMapping("foods")
public ResponseEntity<BaseResponse<Page<FoodAdminResponse>>> getAllFood(
        FoodRequestParam param, @PageableDefault(size = 5, sort="id" ,direction = Sort.Direction.DESC) Pageable pageable ) {
    return ResponseEntity.ok(new BaseResponse<>(
            foodService.getAllFoodAdmin(param,pageable),
            "Get All succsess full"
    ));
}

    @GetMapping("foods/{id}")
    public ResponseEntity<BaseResponse<FoodDetailAdminRespone>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.getById(id),
                "Get ByID succsess full"
        ));
    }

    @PostMapping(value = "foods", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FoodAdminResponse>> create(
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        FoodCreateAndUpdateRequest create =
                new ObjectMapper().readValue(data, FoodCreateAndUpdateRequest.class);

        return ResponseEntity.ok(
                new BaseResponse<>(
                        foodService.createFood(create, image, images),
                        "Create success"
                )
        );
    }

    @PutMapping(value = "foods/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FoodAdminResponse>> update(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        FoodCreateAndUpdateRequest update =
                new ObjectMapper().readValue(data, FoodCreateAndUpdateRequest.class);

        return ResponseEntity.ok(
                new BaseResponse<>(
                        foodService.updateFood(id, update, image, images),
                        "Update success"
                )
        );
    }

    @DeleteMapping("foods{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.deleteFood(id),
                "Delete succsess full"
        ));
    }
//
//
//    // ===== XÓA ẢNH PHỤ =====
//    @DeleteMapping("foods/images/{imageId}")
//    public ResponseEntity<?> deleteSubImage(@PathVariable Long imageId) {
//        foodService.deleteSubImage(imageId);
//        return ResponseEntity.ok("Deleted sub image");
//    }


//    order
    @GetMapping("orders")
    public Page<OrderAdminResponse> getOrders(
            Pageable pageable
    ) {
        return orderService.getOrders(pageable);
    }

    @GetMapping("orders/{id}")
    public OrderDetailAdminResponse getOrderDetail(
            @PathVariable Long id
    ) {
        return orderService.getOrderAdminDetail(id);
    }

    @PutMapping("orders/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {

        orderService.updateStatus(id, status);

        return "Cập nhật trạng thái thành công";
    }
}
