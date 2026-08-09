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
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtUtils jwtUtils;

    // =====================================================
    // SAVE / UPDATE REFRESH TOKEN
    // =====================================================

    @Override
    public RefreshToken save(User user, String token) {

        Optional<RefreshToken> existingToken =
                repository.findByUser(user);

        RefreshToken refreshToken;

        if (existingToken.isPresent()) {

            // User đã đăng nhập trước đó
            // → cập nhật token cũ
            refreshToken = existingToken.get();

        } else {

            // User chưa có refresh token
            // → tạo mới
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        refreshToken.setToken(token);

        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(7)
        );

        return repository.save(refreshToken);
    }

    // =====================================================
    // VERIFY REFRESH TOKEN
    // =====================================================

    @Override
    public RefreshToken verify(String token) {

        // 1. Token null
        if (token == null || token.isBlank()) {

            throw new ApplicationException(
                    "Refresh Token không tồn tại"
            );
        }

        // 2. Validate JWT
        if (!jwtUtils.validateRefreshToken(token)) {

            throw new ApplicationException(
                    "Refresh Token không hợp lệ"
            );
        }

        // 3. Tìm token trong DB
        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new ApplicationException(
                                        "Refresh Token không tồn tại"
                                )
                        );

        // 4. Kiểm tra expiry trong DB
        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            repository.delete(refreshToken);

            throw new ApplicationException(
                    "Refresh Token đã hết hạn"
            );
        }

        // 5. Lấy user
        User user = refreshToken.getUser();

        // 6. Lấy sessionVersion trong refresh token
        Long tokenVersion =
                jwtUtils.getSessionVersionFromToken(token);

        // 7. Lấy sessionVersion hiện tại trong DB
        Long currentVersion =
                user.getSessionVersion();

        // 8. Refresh token thuộc phiên đăng nhập cũ
        if (tokenVersion == null ||
                !tokenVersion.equals(currentVersion)) {

            repository.delete(refreshToken);

            throw new ApplicationException(
                    "Phiên đăng nhập đã hết hiệu lực"
            );
        }

        return refreshToken;
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void delete(RefreshToken token) {

        if (token != null) {
            repository.delete(token);
        }
    }
}