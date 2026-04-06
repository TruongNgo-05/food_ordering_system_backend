package com.example.project_back.repository;


import com.example.project_back.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    //auth

    Optional<Users> findByEmailOrUsername(String email, String username);

    Optional<Users> findByEmail(String email);

}