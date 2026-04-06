package com.example.project_back.repository;


import com.example.project_back.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Integer> {

    Otp findByEmailAndOtp(String email, Integer otp);

    // xóa otp cũ theo email
    void deleteByEmail(String email);

//lấy otp mới nhất theo mail
    Optional<Otp> findTopByEmailOrderByCreatedAtDesc(String email);

}
