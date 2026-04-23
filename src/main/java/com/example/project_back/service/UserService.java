package com.example.project_back.service;

import com.example.project_back.dto.request.admin.AdminUpdateUserRequest;
import com.example.project_back.dto.request.spec.UserRequestParam;
import com.example.project_back.dto.request.user.ChangePasswordRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.response.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
//   user
UserResponse createUser(UserCreateRequest createUserRequest);

UserResponse getCurrentUser();

String uploadAvatar(Long id, MultipartFile file) throws IOException;

UserResponse updateMyProfile(UserUpdateRequest userUpdateRequest);

Boolean changePassword(ChangePasswordRequest change);

String deleteUser(Long id);
//    admin
Page<UserResponse> findAllUsers(UserRequestParam param, Pageable pageable);

UserResponse findUserById(Long id);

UserResponse adminUpdateUser(AdminUpdateUserRequest updateUserRequest, Long id);
}
