package com.example.project_back.service;

import com.example.project_back.dto.authentication.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    // Login
    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);

    // Refresh Access Token
    LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    // Logout
    void logout(HttpServletRequest request, HttpServletResponse response);

    // Forgot password
    String sendOtp(ForgotPassword forgetpw);

    // Reset password
    Boolean resetPassword(ResetPassword resetpw);

    Boolean verifyOtp(VerifyOtpRequest verifyOtpRequest);
}