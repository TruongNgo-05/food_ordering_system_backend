package com.example.project_back.repository;

import com.example.project_back.entity.Conversation;
import com.example.project_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByCustomerId(Long customerId);

    @Query("""
select c
from Conversation c
order by c.updatedAt desc
""")
    List<Conversation> findAllConversation();

//    xoa
List<Conversation> findByCustomerOrStaff(User customer, User staff);

    void deleteByCustomerOrStaff(User customer, User staff);

}