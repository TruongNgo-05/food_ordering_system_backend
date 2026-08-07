package com.example.project_back.service;

import com.example.project_back.dto.chat.ConversationResponse;
import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.StaffSendMessageRequest;

import java.util.List;

public interface StaffChatService {

    List<ConversationResponse> getConversations();

    List<MessageResponse> getMessages(
            Long conversationId,
            Long beforeId,
            int size
    );

    MessageResponse sendMessage(StaffSendMessageRequest request);

    void markAsRead(Long conversationId);
}