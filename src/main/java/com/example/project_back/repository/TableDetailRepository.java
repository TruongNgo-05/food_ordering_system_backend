package com.example.project_back.repository;

import com.example.project_back.constant.TableStatus;
import com.example.project_back.entity.TableDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TableDetailRepository extends JpaRepository<TableDetail,Integer> {
    Optional<TableDetail> findByTableNumber(String tableNumber);


}
