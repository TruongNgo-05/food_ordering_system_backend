package com.example.project_back.service.Impl;

import com.example.project_back.config.JwtUtils;
import com.example.project_back.entity.RefreshToken;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.RefreshTokenRepository;
import com.example.project_back.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtUtils  jwtUtils;

    @Override
    public RefreshToken save(User user, String token) {

        repository.findByUser(user).ifPresent(repository::delete);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);

        refreshToken.setToken(token);

        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        return repository.save(refreshToken);
    }

    @Override
    public RefreshToken verify(String token) {

        // Token null
        if (token == null || token.isBlank()) {
            throw new ApplicationException("Refresh Token không tồn tại");
        }

        // 1. Validate JWT
        if (!jwtUtils.validateRefreshToken(token)) {
            throw new ApplicationException("Refresh Token không hợp lệ");
        }

        // 2. Kiểm tra trong DB
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() ->
                        new ApplicationException("Refresh Token không tồn tại"));

        // 3. Kiểm tra hết hạn trong DB
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            repository.delete(refreshToken);

            throw new ApplicationException("Refresh Token đã hết hạn");
        }

        return refreshToken;
    }

    @Override
    public void delete(User user) {

        repository.deleteByUser(user);

    }
}