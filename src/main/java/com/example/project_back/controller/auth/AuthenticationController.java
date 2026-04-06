package com.example.project_back.controller.auth;


import com.example.project_back.common.BaseResponse;
import com.example.project_back.dto.authentication.ForgotPassword;
import com.example.project_back.dto.authentication.LoginRequest;
import com.example.project_back.dto.authentication.LoginResponse;
import com.example.project_back.dto.authentication.ResetPassword;
import com.example.project_back.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(new BaseResponse<>(
                authenticationService.login(loginRequest) ,
                "Login Succesfull"
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassWork(
            @RequestBody @Valid ForgotPassword forgetpw) {

        String result = authenticationService.sendOtp(forgetpw);

        return ResponseEntity.ok(new BaseResponse<>(
                result,
                "Regain password success"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Boolean>> resetPassword(@RequestBody ResetPassword resetpw) {
        Boolean result = authenticationService.resetPassword(resetpw);
        return ResponseEntity.ok(new BaseResponse<>(result, "Reset password successful!"));
    }
}