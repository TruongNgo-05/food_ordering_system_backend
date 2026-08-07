package com.example.project_back.service.Impl;

import com.example.project_back.config.SecurityUtils;
import com.example.project_back.constant.SenderType;
import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.SendMessageRequest;
import com.example.project_back.entity.Conversation;
import com.example.project_back.entity.Message;
import com.example.project_back.entity.User;
import com.example.project_back.exception.ApplicationException;
import com.example.project_back.repository.ConversationRepository;
import com.example.project_back.repository.MessageRepository;
import com.example.project_back.repository.UserRepository;
import com.example.project_back.service.CustomerChatService;
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
public class CustomerChatServiceImpl implements CustomerChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * Lấy customer đang đăng nhập
     */
    private User getCurrentCustomer() {


            String username = SecurityUtils.getCurrentUsername();

            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new ApplicationException("Không tìm thấy staff"));
    }

    /**
     * Lấy conversation của customer.
     * Nếu chưa có thì tạo mới.
     */
    private Conversation getConversationEntity() {

        User customer = getCurrentCustomer();

        return conversationRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {

                    Conversation conversation = new Conversation();
                    conversation.setCustomer(customer);

                    return conversationRepository.save(conversation);

                });
    }

    /**
     * Lấy id conversation
     */
    @Override
    public Long getConversation() {
        return getConversationEntity().getId();
    }

    /**
     * Danh sách tin nhắn
     */
    @Override
    @Transactional
    public List<MessageResponse> getMessages(Long beforeId, int size) {

        Conversation conversation = getConversationEntity();

        Pageable pageable = PageRequest.of(0, size);

        List<Message> messages;

        if (beforeId == null) {
            messages = messageRepository
                    .findByConversationIdOrderByIdDesc(
                            conversation.getId(),
                            pageable
                    );
        } else {
            messages = messageRepository
                    .findByConversationIdAndIdLessThanOrderByIdDesc(
                            conversation.getId(),
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
                    dto.setIsRead(message.getIsRead());
                    dto.setCreatedAt(message.getCreatedAt());

                    return dto;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(result);

        return result;
    }

    /**
     * Customer gửi tin nhắn
     */
    @Override
    public MessageResponse sendMessage(SendMessageRequest request) {

        User customer = getCurrentCustomer();

        Conversation conversation = getConversationEntity();

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(customer);
        message.setSenderType(SenderType.CUSTOMER);
        message.setContent(request.getContent());

        message = messageRepository.save(message);

        MessageResponse dto = new MessageResponse();
        dto.setId(message.getId());
        dto.setSenderType(message.getSenderType());
        dto.setSenderName(customer.getFullName());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());

        return dto;
    }

    /**
     * Customer đọc tin nhắn của staff
     */
    @Override
    public void markAsRead() {

        Conversation conversation = getConversationEntity();

        messageRepository.markStaffMessagesAsRead(conversation.getId());

    }

}