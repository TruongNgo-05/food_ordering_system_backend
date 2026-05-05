package com.example.project_back.repository;


import com.example.project_back.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByUser_Id(Long userId);
}
