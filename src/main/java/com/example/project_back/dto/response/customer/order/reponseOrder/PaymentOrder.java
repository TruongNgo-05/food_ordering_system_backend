package com.example.project_back.dto.response.customer.order.reponseOrder;

import com.example.project_back.constant.PaymentMethodType;
import com.example.project_back.constant.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOrder {
    private PaymentMethodType paymentMethodType;
    private PaymentStatus paymentStatus;
}
