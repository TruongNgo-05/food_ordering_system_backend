package com.example.project_back.specification;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentMethodType;
import com.example.project_back.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class OrderSpecification {
    public static Specification<Order> hasOrderCode(String orderCode){
        return (root,query,cb)->{
            return cb.like(root.get("orderCode"),"%"+ orderCode.toUpperCase()+"%");
        };
    }

    public static Specification<Order> hasOrderStatus(OrderStatus status){
        return (root,query,cb)->{
            return cb.equal(root.get("status"),status);
        };
    }

    public static Specification<Order> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createdAt"), minDate, maxDate);
        };
    }

//
public static Specification<Order> isOnlineOrder() {
    return (root, query, cb) ->
            cb.like(root.get("orderCode"), "ORD-OL-%");
}
    public static Specification<Order> isTableOrder() {
        return (root, query, cb) ->
                cb.like(root.get("orderCode"), "ORD-TB-%");
    }
}
