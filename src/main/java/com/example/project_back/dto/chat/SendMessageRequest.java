package com.example.project_back.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

}
