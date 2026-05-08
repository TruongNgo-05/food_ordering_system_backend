package com.example.project_back.repository;

import com.example.project_back.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Integer> {
}
