package com.example.project_back.service.Impl;

import com.example.project_back.dto.request.user.CreateUserRequest;
import com.example.project_back.dto.response.user.UserResponse;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.UsersService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse CreateUser(CreateUserRequest createUserRequest) {
        if(userRepository.findByEmailOrUsername(createUserRequest.getEmail(), createUserRequest.getUsername()).isEmpty()){
            throw new ApplicationException("User already exists");
        }
        return null;
    }
}
