package com.example.project_back.repository;

import com.example.project_back.entity.FAQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ,Integer> {

    Page<FAQ> findAllByOrderByIdAsc(Pageable pageable);

}