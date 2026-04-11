package com.example.project_back.repository;


import com.example.project_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    //auth

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByEmail(String email);

    User findUsersByEmail(String email);


}