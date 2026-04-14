package com.example.project_back.controller.User;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.customer.CustomerUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService usersService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponseDTO>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.findAllUsers(pageable),
                "Get All succsess full"
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable  Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.findUserById(id),
                "Get By id User succsess full"
        ));
    }

    @PostMapping()
    public ResponseEntity<BaseResponse <UserResponseDTO>> createUser (@RequestBody @Valid UserCreateRequest createUserRequest){
       return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(
                usersService.createUser(createUserRequest),
                "Create Account Successfully")
        ) ;
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUser(@RequestBody CustomerUpdateRequest customerUpdateRequest , Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.updateUser(id,customerUpdateRequest),
                "Update Account Successfully"
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.deleteUser(id),"delete susecfull"
        ));
    }
    }


