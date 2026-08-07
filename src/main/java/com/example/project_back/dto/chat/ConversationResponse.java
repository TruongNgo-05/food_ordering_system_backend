package com.example.project_back.dto.chat;

import com.example.project_back.constant.ConversationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ConversationResponse {

    private Long conversationId;

    private Long customerId;

    private String customerName;

    private String customerAvatar;

    private String lastMessage;

    private LocalDateTime lastTime;

    private Integer unreadCount;

    private ConversationStatus status;

}