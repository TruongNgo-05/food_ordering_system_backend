package com.example.project_back.specification;

import com.example.project_back.entity.TableDetail;
import org.springframework.data.jpa.domain.Specification;

public class TableSpecification {
    public static Specification<TableDetail> hasTableNumber(String tableNumber){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("tableNumber")),"%"+tableNumber.toUpperCase()+"%");
        };
    }

//    lấy xấp xỉ
    public static Specification<TableDetail> hasCapacity(Integer capacity){
        return (root, query, cb) -> {
            query.orderBy(cb.asc(root.get("capacity")));

            return cb.greaterThanOrEqualTo(root.get("capacity"), capacity);
        };
    }
}
