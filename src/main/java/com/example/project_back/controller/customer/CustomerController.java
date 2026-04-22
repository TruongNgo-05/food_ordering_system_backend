package com.example.project_back.controller.customer;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.response.user.FoodDetailResponse;
import com.example.project_back.service.FoodService;
import com.example.project_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
private final FoodService foodService;

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                userService.deleteUser(id),"delete susecfull"
        ));
    }
    @GetMapping("/foods/{id}")
    public FoodDetailResponse getFoodDetail(@PathVariable Long id){
        return foodService.getFoodDetail(id);
    }
}
