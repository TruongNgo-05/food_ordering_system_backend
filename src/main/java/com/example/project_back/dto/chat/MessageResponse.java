package com.example.project_back.dto.chat;

import com.example.project_back.constant.SenderType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class MessageResponse {

    private Long id;

    private SenderType senderType;

    private String senderName;

    private String content;

    private Boolean isRead;

    private LocalDateTime createdAt;

}