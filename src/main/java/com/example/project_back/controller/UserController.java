package com.example.project_back.controller;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.spec.CategoriesRequestParam;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.request.user.ChangePasswordRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.*;
import com.example.project_back.service.BannerService;
import com.example.project_back.service.CategoriesService;
import com.example.project_back.service.FoodService;
import com.example.project_back.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService usersService;
    private final BannerService bannerService;
    private final CategoriesService categoriesService;
    private final FoodService foodService;

    @PostMapping()
    public ResponseEntity<BaseResponse<UserResponse>> createUser(@RequestBody @Valid UserCreateRequest createUserRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(
                usersService.createUser(createUserRequest),
                "Tạo tài khoản thành công")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> getCurrentUser() {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.getCurrentUser(),
                "Get By Current User sucsess full"
        ));
    }

@PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<BaseResponse<UserResponse>> updateUser(
        @RequestPart("data") String data,
        @RequestPart(value = "avatar", required = false) MultipartFile avatar
) throws Exception {

    UserUpdateRequest request = new ObjectMapper().readValue(data, UserUpdateRequest.class);

    return ResponseEntity.ok(new BaseResponse<>(
                    usersService.updateUser(request, avatar),
                    "Update success"
            )
    );
}

    @PutMapping("/changePassword")
    public ResponseEntity<BaseResponse<Boolean>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.changePassword(changePasswordRequest),
                "Đổi mật khẩu thành công"
        ));
    }

    @GetMapping("/banner")
    public ResponseEntity<BaseResponse<List<BannerResponse>>> getAllBannerCustomer() {
        return ResponseEntity.ok(new BaseResponse<>(
                bannerService.getAllBannerCustomer(),
                "Get All Banner succsess full"
        ));
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<Page<CategoriesResponse>>> getAllCategories(CategoriesRequestParam param, Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.getCategories(param, pageable),
                "get All Categories successfully!"
        ));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<BaseResponse<CategoriesResponse>> getByIdCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.getCategoryById(id),
                "get Category successfully!"
        ));
    }

    @GetMapping("/foods")
    public ResponseEntity<BaseResponse<Page<FoodResponse>>> getAllFood(FoodRequestParam param, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.getAllFoodCustomer(param, pageable),
                "Get All succsess full"
        ));
    }
    @GetMapping("/foods/{id}")
    public ResponseEntity<BaseResponse<FoodDetailResponse>> getFoodDetail(@PathVariable Long id){

        return ResponseEntity.ok(new BaseResponse<>(
                foodService.getFoodDetail(id),
                "Get Food Detail successfully!"
        ));
    }
}





