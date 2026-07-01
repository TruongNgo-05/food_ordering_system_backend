package com.example.project_back.specification;

import com.example.project_back.entity.TableDetail;
import com.example.project_back.entity.TableReservations;
import org.springframework.data.jpa.domain.Specification;

public class TableReservationsSpecification {

    public static Specification<TableReservations> hasCustomerPhone(String customerPhone){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("customerPhone")),"%"+customerPhone.toUpperCase()+"%");
        };
    }
    public static Specification<TableReservations> hasReservationCode(String reservationCode){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("reservationCode")),"%"+reservationCode.toUpperCase()+"%");
        };
    }
}
