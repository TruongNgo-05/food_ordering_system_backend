package com.example.project_back.repository;

import com.example.project_back.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByOrderCode(String orderCode);
}
