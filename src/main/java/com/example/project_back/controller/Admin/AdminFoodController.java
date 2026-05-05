package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.service.FoodService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequestMapping("api/admin/foods")
public class AdminFoodController {
    private final FoodService foodService ;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<FoodAdminResponse>>> getAllFood(
            FoodRequestParam param, @PageableDefault(size = 5, sort="id" ,direction = Sort.Direction.DESC) Pageable pageable ) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.getAllFoodAdmin(param,pageable),
                "Get All succsess full"
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<FoodDetailAdminRespone>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.getById(id),
                "Get ByID succsess full"
        ));
    }

@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.deleteFood(id),
                "Delete succsess full"
        ));
    }
//
//
//    // ===== XÓA ẢNH PHỤ =====
//    @DeleteMapping("/images/{imageId}")
//    public ResponseEntity<?> deleteSubImage(@PathVariable Long imageId) {
//        foodService.deleteSubImage(imageId);
//        return ResponseEntity.ok("Deleted sub image");
//    }
}
