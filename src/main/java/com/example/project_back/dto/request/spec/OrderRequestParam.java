package com.example.project_back.dto.request.spec;

import com.example.project_back.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class OrderRequestParam {
    private String orderCode;
    private OrderStatus status;
    private LocalDate minDate;
    private LocalDate maxDate;
}
