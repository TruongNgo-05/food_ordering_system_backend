package com.example.project_back.dto.request.customer.order;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateOrderRequest {

    private Integer paymentMethodId;

    private Integer voucherId;

    private Integer addressId;

    private String voucherCode;

    private String note;
}
