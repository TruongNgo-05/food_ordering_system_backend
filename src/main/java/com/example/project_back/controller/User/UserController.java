package com.example.project_back.controller.User;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.service.UserService;
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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService usersService;


    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserResponseDTO>>> getAllUsers(@PageableDefault(size = 5, sort="id" ,direction = Sort.Direction.DESC) Pageable pageable ) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.findAllUsers(pageable),
                "Get All succsess full"
        ));
    }

    @GetMapping("{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
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
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getCurrentUser(){
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.getCurrentUser(),
                "Get By Current User sucsess full"
        ));
    }

    @PutMapping("/me")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateMyProfile(@RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(new BaseResponse<>(
                usersService.updateMyProfile(userUpdateRequest),
                "Update Account Successfully"
        ));
    }


    @PostMapping(value = "/upload-avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String avatarUrl = usersService.uploadAvatar(id, file);
            return ResponseEntity.ok(avatarUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    }


