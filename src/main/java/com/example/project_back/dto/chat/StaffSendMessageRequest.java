package com.example.project_back.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffSendMessageRequest {
    @NotNull
    private Long conversationId;

    @NotBlank
    private String content;
}
