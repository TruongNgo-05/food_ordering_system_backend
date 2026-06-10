package com.example.project_back.specification;

import com.example.project_back.dto.request.spec.OrderRequestParam;
import com.example.project_back.entity.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecificationBuilder {

    public static Specification<Order> build(
            OrderRequestParam param
    ) {

        Specification<Order> spec = Specification.unrestricted();

        if (param.getOrderCode() != null && !param.getOrderCode().isBlank()) {
            spec = spec.and(OrderSpecification.hasOrderCode(param.getOrderCode()));
        }

        if (param.getStatus() != null) {
            spec = spec.and(OrderSpecification.hasOrderStatus(param.getStatus()));
        }

        if (param.getMinDate() != null && param.getMaxDate() != null) {
            spec = spec.and(OrderSpecification.hasCreateDate(param.getMinDate(), param.getMaxDate()));
        }

        return spec;
    }
}