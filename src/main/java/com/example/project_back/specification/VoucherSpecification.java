package com.example.project_back.specification;

import com.example.project_back.entity.Voucher;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VoucherSpecification {
    public static Specification<Voucher> hasVoucherCode(String code){
        return (root, query, criteriaBuilder) ->{
            return criteriaBuilder.like(root.get("code"),"%"+ code.toUpperCase()+"%");
        };
    }

    public static Specification<Voucher> hasDateVoucher(LocalDateTime startDate, LocalDateTime endDate){
        return (root, query, cb) ->
                cb.and(
                        cb.greaterThanOrEqualTo(root.get("startDate"), startDate),
                        cb.lessThanOrEqualTo(root.get("endDate"), endDate)
                );
    }
    }
