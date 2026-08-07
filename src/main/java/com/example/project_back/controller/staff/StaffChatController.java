package com.example.project_back.controller.staff;

import com.example.project_back.dto.chat.ConversationResponse;
import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.StaffSendMessageRequest;
import com.example.project_back.service.StaffChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/chat")
@RequiredArgsConstructor
public class StaffChatController {

    private final StaffChatService staffChatService;

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversations() {
        return staffChatService.getConversations();
    }

    @GetMapping("/messages/{conversationId}")
    public List<MessageResponse> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "5") int size) {

        return staffChatService.getMessages(conversationId, beforeId, size);
    }

    @PostMapping("/send")
    public MessageResponse sendMessage(
            @Valid @RequestBody StaffSendMessageRequest request) {

        return staffChatService.sendMessage(request);
    }

    @PutMapping("/read/{conversationId}")
    public void markAsRead(
            @PathVariable Long conversationId) {

        staffChatService.markAsRead(conversationId);
    }

}