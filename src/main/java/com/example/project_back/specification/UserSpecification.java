package com.example.project_back.specification;

import com.example.project_back.constant.Role;
import com.example.project_back.constant.Status;
import com.example.project_back.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<User> hasEmail(String email){
        return (root, query, cb) -> {
            return cb.like(cb.upper(root.get("email")),"%"+ email.toUpperCase()+"%");
        };
    }

    public static Specification<User> hasFullName(String fullName){
        return (root,query,cb)->{
            return cb.like(cb.upper(root.get("fullName")),"%"+ fullName.toUpperCase()+"%");
        };
    }

    public static Specification<User> hasRole(Role role){
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("role"), role);
        };
    }

    public static Specification<User> hasStatus(Status status){
        return  (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<User> hasCreateDate(LocalDate minDate, LocalDate maxDate) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.between(root.get("createDate"), minDate, maxDate);
        };
    }
}
