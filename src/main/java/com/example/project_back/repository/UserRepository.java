package com.example.project_back.repository;


import com.example.project_back.constant.Role;
import com.example.project_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    //auth

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);
}