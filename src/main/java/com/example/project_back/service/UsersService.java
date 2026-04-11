package com.example.project_back.service;

import com.example.project_back.dto.request.user.CreateUserRequest;
import com.example.project_back.dto.response.user.UserResponse;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
   UserResponse CreateUser (CreateUserRequest createUserRequest);
}
