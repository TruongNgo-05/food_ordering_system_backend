
package com.example.project_back.config;

import com.example.project_back.entity.User;
import com.example.project_back.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/api/auth")
                || path.startsWith("/oauth2")
                || path.startsWith("/login");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        // ==================================================
        // KHÔNG CÓ ACCESS TOKEN
        // ==================================================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7);


        // ==================================================
        // 1. VALIDATE ACCESS TOKEN
        // ==================================================

        if (!jwtUtils.validateAccessToken(token)) {

            unauthorized(response, "Access Token không hợp lệ hoặc đã hết hạn");
            return;
        }


        // ==================================================
        // 2. GET USERNAME
        // ==================================================

        String username;

        try {

            username =
                    jwtUtils.getUsernameFromToken(token);

        } catch (Exception e) {

            unauthorized(response, "Access Token không hợp lệ");
            return;
        }


        // ==================================================
        // 3. GET USER
        // ==================================================

        Optional<User> optionalUser =
                userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {

            unauthorized(response, "User không tồn tại");
            return;
        }

        User user =
                optionalUser.get();


        // ==================================================
        // 4. GET SESSION VERSION
        // ==================================================

        Long tokenVersion =
                jwtUtils.getSessionVersionFromToken(token);

        Long currentVersion =
                user.getSessionVersion();


        // ==================================================
        // JWT CŨ KHÔNG CÓ SESSION VERSION
        // ==================================================

        if (tokenVersion == null) {

            unauthorized(
                    response,
                    "Access Token không còn hợp lệ"
            );

            return;
        }


        // ==================================================
        // 5. CHECK SESSION VERSION
        // ==================================================

        if (!tokenVersion.equals(currentVersion)) {

            unauthorized(
                    response,
                    "Phiên đăng nhập đã hết hạn"
            );

            return;
        }


        // ==================================================
        // 6. AUTHENTICATION
        // ==================================================

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);


        // ==================================================
        // 7. CONTINUE
        // ==================================================

        filterChain.doFilter(request, response);
    }


    // ======================================================
    // RETURN 401
    // ======================================================

    private void unauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                "application/json;charset=UTF-8"
        );

        response.getWriter().write(
                "{\"message\":\"" + message + "\"}"
        );
    }
}