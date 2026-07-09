package com.example.project_back.dto.request.spec;

import com.example.project_back.constant.Status;
import com.example.project_back.constant.SupportStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SupportRequestParam {

    private String supportCode;

    private SupportStatus status;

    private LocalDateTime minDate;

    private LocalDateTime maxDate;
}
