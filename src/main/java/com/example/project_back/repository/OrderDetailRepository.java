package com.example.project_back.repository;

import com.example.project_back.entity.Order;
import com.example.project_back.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderCode = :orderCode")
    List<OrderDetail> findByOrderOrderCode(@Param("orderCode") String orderCode);
}
