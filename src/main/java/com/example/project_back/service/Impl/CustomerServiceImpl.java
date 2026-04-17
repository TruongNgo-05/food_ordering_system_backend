package com.example.project_back.service.Impl;

import com.example.project_back.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl {

    private final UserRepository userRepository;


}
