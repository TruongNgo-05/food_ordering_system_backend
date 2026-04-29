package com.example.project_back.dto.request.spec;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherRequestParam {
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
