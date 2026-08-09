package com.example.project_back.security.oauth;

import com.example.project_back.config.JwtUtils;
import com.example.project_back.config.UsernameGenerator;
import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.entity.User;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.Impl.ContentMailService;
import com.example.project_back.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final ContentMailService  contentMailService;

    @Value("${app.frontend-url}") private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // provider: google / facebook
        String provider = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatar = null;

        // ==============================
        //  GOOGLE
        // ==============================
        if ("google".equals(provider)) {
            avatar = oAuth2User.getAttribute("picture");
        }

        // ==============================
        //  FACEBOOK
        // ==============================
        if ("facebook".equals(provider)) {

            Map<String, Object> picture =
                    oAuth2User.getAttribute("picture");

            if (picture != null) {

                Map<String, Object> data =
                        (Map<String, Object>) picture.get("data");

                if (data != null) {
                    avatar = (String) data.get("url");
                }
            }

            if (email == null) {
                email = oAuth2User.getAttribute("id") + "@facebook.com";
            }
        }

        boolean isNewUser = false;

        User user = userRepository.findByEmail(email).orElse(null);

        // ==============================
        // BLOCKED USER
        // ==============================
        if (user != null && user.getStatus() == Status.LOCKED) {

            response.sendRedirect(
                    frontendUrl + "/login?error=locked"
            );

            return;
        }

        // ==============================
        // CREATE NEW USER
        // ==============================
        if (user == null) {
            isNewUser = true;

            user = new User();
            user.setEmail(email);
            user.setUsername(
                    UsernameGenerator.generateUniqueUsername(name, userRepository)
            );

            user.setFullName(name);
            user.setAvatar(avatar);
            user.setRole(Role.CUSTOMER);
            user.setStatus(Status.ACTIVED);
            user.setIsActive(true);
            user.setCreatedDate(LocalDateTime.now());
            user.setFailCount(0);
            user.setLockTime(null);
            user.setSessionVersion(0L);

            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            userRepository.save(user);
            contentMailService.sendRegisterSuccess(user);
        }
        // ==============================
// LOGIN SUCCESS
// ==============================

        user.setSessionVersion(
                user.getSessionVersion() + 1
        );

        userRepository.save(user);

        contentMailService.sendLoginSuccess(user);

// ==============================
// GENERATE JWT
// ==============================

        String accessToken =
                jwtUtils.generateAccessToken(user);

        String refreshToken =
                jwtUtils.generateRefreshToken(user);

// Lưu refresh token vào DB
        refreshTokenService.save(user, refreshToken);

// Lưu refresh token vào HttpOnly Cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);

// Khi deploy HTTPS thì đổi thành true
        cookie.setSecure(false);

        cookie.setPath("/");

// 7 ngày
        cookie.setMaxAge(7 * 24 * 60 * 60);

// Nếu frontend và backend khác domain trong production,
// bạn sẽ cần cấu hình thêm SameSite=None.
        response.addCookie(cookie);

// Chỉ trả access token cho React
        response.sendRedirect(
                frontendUrl + "/oauth-success"
                        + "?accessToken=" + accessToken
                        + "&new=" + isNewUser
        );
    }

}