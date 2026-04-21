package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.FoodCreateAndUpdateRequest;
import com.example.project_back.dto.request.spec.FoodRequestParam;
import com.example.project_back.dto.response.admin.FoodAdminResponse;
import com.example.project_back.dto.response.admin.FoodDetailAdminRespone;
import com.example.project_back.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/admin/foods")
public class AdminFoodController {
    private final FoodService foodService ;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<FoodAdminResponse>>> getAllFood(FoodRequestParam param, @PageableDefault(size = 5, sort="id" ,direction = Sort.Direction.DESC) Pageable pageable ) {
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

    @PostMapping
    public ResponseEntity<BaseResponse<FoodAdminResponse>> create(@RequestBody FoodCreateAndUpdateRequest create) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.createFood(create),
                "Create succsess full"
        ));
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<FoodAdminResponse>> update(@RequestBody FoodCreateAndUpdateRequest update , @PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.updateFood(update,id),
                "Update succsess full"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                foodService.deleteFood(id),
                "Delete succsess full"
        ));
    }
}
