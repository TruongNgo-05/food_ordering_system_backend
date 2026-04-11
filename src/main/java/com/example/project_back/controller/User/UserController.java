package com.example.project_back.controller.User;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.user.CreateUserRequest;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UsersService usersService;

    @PostMapping()
    public ResponseEntity<BaseResponse <UserResponse>> CreateUser (@RequestBody @Valid CreateUserRequest createUserRequest){
       return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(
                usersService.CreateUser(createUserRequest),
                "Create Account Successfully")
        ) ;
    }

    }


