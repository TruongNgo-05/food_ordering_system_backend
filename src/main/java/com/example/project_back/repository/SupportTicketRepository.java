package com.example.project_back.repository;

import com.example.project_back.entity.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket,Integer>, JpaSpecificationExecutor<SupportTicket> {


    Page<SupportTicket> findByUserIdOrderByCreatedAtDesc(
            Long userId,
            Pageable pageable
    );

}