package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.admin.CategoriesCreateAndUpdate;
import com.example.project_back.dto.response.user.CategoriesResponse;
import com.example.project_back.service.CategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/admin/categories")
public class AdminCategoriesController {
    private final CategoriesService categoriesService;


    @PostMapping
    public ResponseEntity<BaseResponse<CategoriesResponse>> createCategories(@RequestBody CategoriesCreateAndUpdate create) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.createCategories(create),
                "Create Categories Admin successfully!"
        ));
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<CategoriesResponse>> updateCategories(@RequestBody CategoriesCreateAndUpdate update,@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.updateCategories(update,id),
                "Update Categories Admin successfully!"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteCategories(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                categoriesService.deleteCategories(id),
                "Delete Categories Admin successfully!"
        ));
    }

}
