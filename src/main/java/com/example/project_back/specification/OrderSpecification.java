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


public static Specification<Order> isOnlineOrCodOrder() {
    return (root, query, cb) -> cb.or(
            cb.like(root.get("orderCode"), "ORDO%"),
            cb.like(root.get("orderCode"), "ORDC%")
    );
}

    public static Specification<Order> isTableOrder() {
        return (root, query, cb) ->
                cb.like(root.get("orderCode"), "ORDT%");
    }

    public static Specification<Order> hasCreatedAtBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return cb.between(
                        root.get("createdAt"),
                        from.atStartOfDay(),
                        to.atTime(23, 59, 59)
                );
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
            }

            return cb.lessThanOrEqualTo(root.get("createdAt"), to.atTime(23, 59, 59));
        };
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Order> orderByStatusPriority() {
        return (root, query, cb) -> {

            query.orderBy(
                    cb.asc(
                            cb.selectCase(root.get("status"))
                                    .when(OrderStatus.PENDING, 1)
                                    .when(OrderStatus.CONFIRMED, 2)
                                    .when(OrderStatus.PREPARING, 3)
                                    .when(OrderStatus.DELIVERING, 4)
                                    .when(OrderStatus.CANCELED, 5)
                                    .when(OrderStatus.REJECTED, 6)
                                    .when(OrderStatus.COMPLETED, 999)
                                    .otherwise(1000)
                    ),
                    cb.desc(root.get("createdAt"))
            );

            return null;
        };
    }
}
