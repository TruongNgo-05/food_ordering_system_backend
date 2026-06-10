package com.example.project_back.repository;

import com.example.project_back.constant.TableStatus;
import com.example.project_back.entity.TableDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TableDetailRepository extends JpaRepository<TableDetail,Integer>, JpaSpecificationExecutor<TableDetail> {
    Optional<TableDetail> findByTableNumber(String tableNumber);

    boolean existsByTableNumber(String tableNumber);
}
