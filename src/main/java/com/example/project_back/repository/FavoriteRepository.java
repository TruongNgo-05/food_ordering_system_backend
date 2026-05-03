package com.example.project_back.repository;

import com.example.project_back.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Integer> {
    Optional<Favorite> findByUser_IdAndFood_Id(Long userId, Long foodId);

    List<Favorite> findByUser_Id(Long userId);

    void deleteByUser_IdAndFood_Id(Long userId, Long foodId);
}
