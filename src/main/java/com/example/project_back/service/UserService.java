package com.example.project_back.service;

import com.example.project_back.dto.request.user.UserUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
//   user
UserResponseDTO createUser(UserCreateRequest createUserRequest);

UserResponseDTO getCurrentUser();

UserResponseDTO updateMyProfile(UserUpdateRequest userUpdateRequest);

String deleteUser(Long id);
//    admin
Page<UserResponseDTO> findAllUsers(Pageable pageable);

UserResponseDTO findUserById(Long id);


}
