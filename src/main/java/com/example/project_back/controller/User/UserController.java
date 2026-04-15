package com.example.project_back.controller.User;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import com.example.project_back.entity.User;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.FileService;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService usersService;
private final UserRepository userRepository;
    private final FileService fileService;


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


    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        String fileName = fileService.uploadFile(file);

        return ResponseEntity.ok(
                "Upload thành công: " + fileName
        );
    }


    @PostMapping(value = "/upload-avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            String oldAvatarUrl = user.getAvatar();

            String oldFileName = oldAvatarUrl.substring(oldAvatarUrl.lastIndexOf("/") + 1);

            Path oldFilePath = Paths.get("uploads").resolve(oldFileName);

            Files.deleteIfExists(oldFilePath);
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("File must be image");
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        String avatarUrl = "http://localhost:8080/uploads/" + fileName;

        user.setAvatar(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok(avatarUrl);
    }
    }


