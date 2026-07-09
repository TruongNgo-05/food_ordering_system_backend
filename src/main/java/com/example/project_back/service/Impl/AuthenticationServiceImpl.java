package com.example.project_back.service.Impl;


import com.example.project_back.config.JwtUtils;
import com.example.project_back.constant.Status;
import com.example.project_back.dto.authentication.*;
import com.example.project_back.entity.Otp;
import com.example.project_back.entity.RefreshToken;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.OtpRepository;
import com.example.project_back.repository.RefreshTokenRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.AuthenticationService;
import com.example.project_back.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {


    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final RefreshTokenService refreshTokenService;
    private final ContentMailService  contentMailService;



    @Override
    public LoginResponse login(LoginRequest request,
                               HttpServletResponse response) {

        Optional<User> users = userRepository.findByEmailOrUsername(
                request.getEmailOrUsername(),
                request.getEmailOrUsername()
        );

        if (users.isEmpty()) {
            throw new ApplicationException("Sai email hoặc username");
        }

        User user = users.get();

        // Kiểm tra khóa tài khoản
        if (user.getStatus() == Status.LOCKED && user.getLockTime() != null) {

            if (user.getLockTime().plusMinutes(15).isBefore(LocalDateTime.now())) {

                user.setStatus(Status.ACTIVED);
                user.setFailCount(0);
                user.setLockTime(null);

                userRepository.save(user);

            } else {
                throw new ApplicationException("Tài khoản bị khóa. Thử lại sau 15 phút");
            }
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            int failCount = user.getFailCount() + 1;

            user.setFailCount(failCount);

            if (failCount >= 5) {

                user.setStatus(Status.LOCKED);

                user.setLockTime(LocalDateTime.now());

                contentMailService.sendAccountLocked(user);
            }

            userRepository.save(user);

            throw new ApplicationException(
                    "Sai mật khẩu. Bạn còn " + (5 - failCount) + "/5 lần thử"
            );
        }

        user.setFailCount(0);

        userRepository.save(user);

      contentMailService.sendLoginSuccess(user);

        String accessToken = jwtUtils.generateAccessToken(user.getUsername());

        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        refreshTokenService.save(user, refreshToken);

        Cookie cookie = new Cookie("refreshToken", refreshToken);

        cookie.setHttpOnly(true);

        cookie.setSecure(false); // true khi deploy HTTPS

        cookie.setPath("/");

        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return new LoginResponse(
                accessToken,
                "Bearer",
                user.getUsername(),
                user.getRole().name(),
                user.getFailCount()
        );
    }

    @Override
    public LoginResponse refreshToken(HttpServletRequest request,
                                      HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new ApplicationException("Không tìm thấy Refresh Token");
        }

        String refreshToken = null;

        for (Cookie cookie : cookies) {

            if ("refreshToken".equals(cookie.getName())) {

                refreshToken = cookie.getValue();

                break;
            }
        }

        if (refreshToken == null) {
            throw new ApplicationException("Refresh Token không tồn tại");
        }

        RefreshToken token = refreshTokenService.verify(refreshToken);

        User user = token.getUser();

        String accessToken = jwtUtils.generateAccessToken(user.getUsername());

        return new LoginResponse(
                accessToken,
                "Bearer",
                user.getUsername(),
                user.getRole().name(),
                user.getFailCount()
        );
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            for (Cookie cookie : cookies) {

                if ("refreshToken".equals(cookie.getName())) {

                    RefreshToken token = refreshTokenService.verify(cookie.getValue());

                    refreshTokenService.delete(token.getUser());

                    Cookie deleteCookie = new Cookie("refreshToken", null);

                    deleteCookie.setHttpOnly(true);

                    deleteCookie.setSecure(false);

                    deleteCookie.setPath("/");

                    deleteCookie.setMaxAge(0);

                    response.addCookie(deleteCookie);

                    break;
                }
            }
        }
    }

    @Transactional
    @Override
    public String sendOtp(ForgotPassword forgetpw) {

        Optional<User> user = userRepository.findByEmail(forgetpw.getEmail());

        if (user.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản");
        }

        // Kiểm tra thời gian gửi OTP gần nhất
        Optional<Otp> lastOtp =
                otpRepository.findTopByEmailOrderByCreatedAtDesc(forgetpw.getEmail());

        if (lastOtp.isPresent()) {

            LocalDateTime lastTime = lastOtp.get().getCreatedAt();

            // Chỉ cho gửi lại sau 60 giây
            if (lastTime.plusSeconds(60).isAfter(LocalDateTime.now())) {
                throw new ApplicationException("Vui lòng chờ 60 giây trước khi gửi lại OTP.");
            }
        }

        // Xóa OTP cũ
        otpRepository.deleteByEmail(forgetpw.getEmail());

        // Tạo OTP
        int otp = new Random().nextInt(900000) + 100000;

        Otp newOtp = new Otp();
        newOtp.setEmail(forgetpw.getEmail());
        newOtp.setOtp(otp);

        // OTP có hiệu lực 5 phút
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setExpireAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(newOtp);

        log.info("Send OTP for user {} : {}", forgetpw.getEmail(), otp);

        contentMailService.sendOtp(user.get(), otp);

        return "OTP đã được gửi qua email: " + forgetpw.getEmail();
    }

    @Override
    public Boolean resetPassword(ResetPassword resetpw) {

        Optional<User> users = userRepository.findByEmail(resetpw.getEmail());
        if (users.isEmpty()) {
            throw new ApplicationException("Không tìm thấy tài khoản người dùng");
        }

        Otp otp = otpRepository.findByEmailAndOtp(resetpw.getEmail(), resetpw.getOtp());

        if (otp == null) {
            throw new ApplicationException("OTP không đúng");
        }

        if (otp.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException("OTP đã hết hạn");
        }

        if (!resetpw.getNewPassword().equals(resetpw.getConfirmNewPassword())) {
            throw new ApplicationException("mật khẩu mới không khớp");
        }

        User user = users.get();
        user.setPassword(passwordEncoder.encode(resetpw.getNewPassword()));
        userRepository.save(users.get());
        otpRepository.delete(otp);
        contentMailService.sendPasswordChanged(user);

        return true;
    }
}
