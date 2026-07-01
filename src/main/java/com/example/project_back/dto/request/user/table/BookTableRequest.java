package com.example.project_back.dto.request.user.table;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class    BookTableRequest {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Integer tableId;
    private String note;
    private LocalDateTime timeComes;
}
