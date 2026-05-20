package com.example.project_back.specification;

import com.example.project_back.entity.Order;
import com.example.project_back.entity.Voucher;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    public Specification<Order> hasOrderCode(String orderCode){
        return (root,query,cb)->{
            return cb.equal(root.get("orderCode"),"%"+ orderCode.toUpperCase()+"%");
        };
    }

    public Specification<Order> hasOrderStatus(String orderStatus){
        return (root,query,cb)->{
            return cb.equal(root.get("orderStatus"),orderStatus);
        };
    }

    public static Specification<Voucher> hasDateOrder(LocalDateTime startDate, LocalDateTime endDate){
        return (root, query, cb) ->
                cb.and(
                        cb.greaterThanOrEqualTo(root.get("startDate"), startDate),
                        cb.lessThanOrEqualTo(root.get("endDate"), endDate)
                );
    }
}
