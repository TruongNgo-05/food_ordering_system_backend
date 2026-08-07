package com.example.project_back.repository;

import com.example.project_back.constant.SenderType;
import com.example.project_back.entity.Conversation;
import com.example.project_back.entity.Message;
import com.example.project_back.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByIdDesc(
            Long conversationId,
            Pageable pageable
    );

    List<Message> findByConversationIdAndIdLessThanOrderByIdDesc(
            Long conversationId,
            Long beforeId,
            Pageable pageable
    );
    @Modifying
    @Query("""
        update Message m
        set m.isRead = true
        where m.conversation.id = :conversationId
        and m.senderType = com.example.project_back.constant.SenderType.STAFF
        and m.isRead = false
    """)
    void markStaffMessagesAsRead(Long conversationId);

//staff
    Integer countByConversationIdAndSenderTypeAndIsReadFalse(
            Long conversationId,
            SenderType senderType
    );

    @Modifying
    @Query("""
update Message m
set m.isRead=true
where m.conversation.id=:conversationId
and m.senderType=com.example.project_back.constant.SenderType.CUSTOMER
and m.isRead=false
""")
    void markCustomerMessagesAsRead(Long conversationId);

    Message findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);


//xoa
void deleteByConversationIn(List<Conversation> conversations);

    void deleteBySender(User sender);

}