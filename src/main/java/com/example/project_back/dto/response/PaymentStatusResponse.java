package com.example.project_back.dto.response;

import com.example.project_back.constant.OrderStatus;
import com.example.project_back.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusResponse {
    private String orderCode;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;
}
