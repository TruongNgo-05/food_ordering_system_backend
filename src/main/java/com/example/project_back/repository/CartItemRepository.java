package com.example.project_back.repository;

import com.example.project_back.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCart_IdAndFood_Id(Integer cartId, Long foodId);
}