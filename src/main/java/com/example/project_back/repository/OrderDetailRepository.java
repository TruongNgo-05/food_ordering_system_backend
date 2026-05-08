package com.example.project_back.repository;

import com.example.project_back.entity.Order;
import com.example.project_back.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer> {
    Optional<Order> findByOrderCode(String orderCode);
}
