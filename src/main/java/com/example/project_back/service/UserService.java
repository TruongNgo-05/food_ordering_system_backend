package com.example.project_back.service;

import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
//   user
UserResponseDTO createUser(UserCreateRequest createUserRequest);

UserResponseDTO getCurrentUser();

String uploadAvatar(Long id, MultipartFile file) throws IOException;

UserResponseDTO updateMyProfile(UserUpdateRequest userUpdateRequest);

String deleteUser(Long id);
//    admin
Page<UserResponseDTO> findAllUsers(Pageable pageable);

UserResponseDTO findUserById(Long id);


}
