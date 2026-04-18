package com.example.project_back.controller.Admin;

import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/admin/users")
public class AdminUserController {
    private final UserService usersService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponse>>> getAllUsers(UserRequestParam param, @PageableDefault(size = 5, sort="id" ,direction = Sort.Direction.DESC) Pageable pageable ) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.findAllUsers(param,pageable),
                "Get All succsess full"
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.findUserById(id),
                "Get By id User succsess full"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.deleteUser(id),
                "Delete User succsess full"
        ));
    }
}
