package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.SenderType;
import com.example.project_back.dto.chat.ConversationResponse;
import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.StaffSendMessageRequest;
import com.example.project_back.entity.Conversation;
import com.example.project_back.entity.Message;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.ConversationRepository;
import com.example.project_back.repository.MessageRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.StaffChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffChatServiceImpl implements StaffChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private User getCurrentStaff() {

        String username = SecurityUtils.getCurrentUsername();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("Không tìm thấy staff"));
    }

    @Override
    @Transactional
    public List<ConversationResponse> getConversations() {

        return conversationRepository.findAllConversation()
                .stream()
                .map(conversation -> {

                    ConversationResponse dto = new ConversationResponse();

                    dto.setConversationId(conversation.getId());

                    dto.setCustomerId(conversation.getCustomer().getId());

                    dto.setCustomerName(conversation.getCustomer().getFullName());

                    dto.setCustomerAvatar(conversation.getCustomer().getAvatar());

                    dto.setStatus(conversation.getStatus());

                    Message lastMessage =
                            messageRepository.findTopByConversationIdOrderByCreatedAtDesc(conversation.getId());

                    if (lastMessage != null) {
                        dto.setLastMessage(lastMessage.getContent());
                        dto.setLastTime(lastMessage.getCreatedAt());
                    }
// Đếm số tin nhắn customer chưa đọc
                    dto.setUnreadCount(
                            messageRepository.countByConversationIdAndSenderTypeAndIsReadFalse(
                                    conversation.getId(),
                                    SenderType.CUSTOMER
                            )
                    );

                    return dto;

                }).toList();

    }

    @Override
    @Transactional
    public List<MessageResponse> getMessages(Long conversationId, Long beforeId, int size) {

        Pageable pageable = PageRequest.of(0, size);

        List<Message> messages;

        if (beforeId == null) {
            messages = messageRepository
                    .findByConversationIdOrderByIdDesc(
                            conversationId,
                            pageable
                    );
        } else {
            messages = messageRepository
                    .findByConversationIdAndIdLessThanOrderByIdDesc(
                            conversationId,
                            beforeId,
                            pageable
                    );
        }

        List<MessageResponse> result = messages.stream()
                .map(message -> {

                    MessageResponse dto = new MessageResponse();

                    dto.setId(message.getId());
                    dto.setSenderType(message.getSenderType());

                    if (message.getSender() != null) {
                        dto.setSenderName(message.getSender().getFullName());
                    }

                    dto.setContent(message.getContent());
                    dto.setCreatedAt(message.getCreatedAt());
                    dto.setIsRead(message.getIsRead());

                    return dto;

                })
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(result);

        return result;
    }

    @Override
    public MessageResponse sendMessage(StaffSendMessageRequest request) {

        User staff = getCurrentStaff();

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation không tồn tại"));

        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(staff);
        message.setSenderType(SenderType.STAFF);
        message.setContent(request.getContent());

        messageRepository.save(message);

        MessageResponse dto = new MessageResponse();

        dto.setId(message.getId());
        dto.setSenderType(message.getSenderType());
        dto.setSenderName(staff.getFullName());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setIsRead(message.getIsRead());

        return dto;

    }

    @Override
    public void markAsRead(Long conversationId) {

        messageRepository.markCustomerMessagesAsRead(conversationId);

    }

}