package com.example.project_back.service;

import com.example.project_back.dto.request.customer.CustomerUpdateRequest;
import com.example.project_back.dto.request.user.UserCreateRequest;
import com.example.project_back.dto.response.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findUserById(Long id);

    UserResponseDTO createUser(UserCreateRequest createUserRequest);

    UserResponseDTO updateUser(Long id, CustomerUpdateRequest customerUpdateRequest);

    String deleteUser(Long id);
}
