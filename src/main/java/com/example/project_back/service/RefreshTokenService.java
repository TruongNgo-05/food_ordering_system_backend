package com.example.project_back.service;

import com.example.project_back.entity.RefreshToken;
import com.example.project_back.entity.User;

public interface RefreshTokenService {

    RefreshToken save(User user, String token);

    RefreshToken verify(String token);

    void delete(User user);

}