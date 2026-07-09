package com.example.project_back.specification;

import com.example.project_back.constant.SupportStatus;

import com.example.project_back.entity.Order;
import com.example.project_back.entity.SupportTicket;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupportSpecification {
    public static Specification<SupportTicket> hasSupportCode(String supportCode) {
        return (root, query, cb) ->
                cb.like(
                        cb.upper(root.get("supportCode")),
                        "%" + supportCode.toUpperCase() + "%"
                );
    }

    public static Specification<SupportTicket> hasStatus(SupportStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<SupportTicket> hasCreateDate(LocalDateTime minDate,
                                                             LocalDateTime maxDate) {
        return (root, query, cb) ->
                cb.between(root.get("createdAt"), minDate, maxDate);
    }


}
