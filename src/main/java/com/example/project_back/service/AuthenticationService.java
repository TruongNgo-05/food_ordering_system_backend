package com.example.project_back.service;

import com.example.project_back.dto.authentication.ForgotPassword;
import com.example.project_back.dto.authentication.LoginRequest;
import com.example.project_back.dto.authentication.LoginResponse;
import com.example.project_back.dto.authentication.ResetPassword;

public interface AuthenticationService {

    // USER
    LoginResponse login(LoginRequest loginRequest );

    String sendOtp(ForgotPassword forgetpw);

    Boolean resetPassword(ResetPassword resetpw);
}
