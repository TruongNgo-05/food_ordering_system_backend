package com.example.project_back.controller.customer;

import com.example.project_back.dto.chat.MessageResponse;
import com.example.project_back.dto.chat.SendMessageRequest;
import com.example.project_back.service.CustomerChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/chat")
@RequiredArgsConstructor
public class CustomerChatController {

    private final CustomerChatService customerChatService;

    /**
     * Lấy hoặc tạo conversation
     */
    @GetMapping("/conversation")
    public Map<String, Long> getConversation() {

        Long conversationId = customerChatService.getConversation();

        Map<String, Long> response = new HashMap<>();
        response.put("conversationId", conversationId);

        return response;
    }

    /**
     * Lấy toàn bộ tin nhắn
     */
    @GetMapping("/messages")
    public List<MessageResponse> getMessages(
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "5") int size
    ) {
        return customerChatService.getMessages(beforeId, size);
    }

    /**
     * Customer gửi tin nhắn
     */
    @PostMapping("/send")
    public MessageResponse sendMessage(
            @Valid @RequestBody SendMessageRequest request) {

        return customerChatService.sendMessage(request);
    }

    /**
     * Đánh dấu đã đọc
     */
    @PutMapping("/read")
    public void markAsRead() {
        customerChatService.markAsRead();
    }

}