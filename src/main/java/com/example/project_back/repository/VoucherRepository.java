package com.example.project_back.repository;

import com.example.project_back.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Integer>, JpaSpecificationExecutor<Voucher> {
    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Integer id);
}
