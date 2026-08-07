package com.example.project_back.service;

import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.SendMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerChatService {
    Long getConversation();

    List<MessageResponse> getMessages(Long beforeId, int size);

    MessageResponse sendMessage(SendMessageRequest request);

    void markAsRead();
}
