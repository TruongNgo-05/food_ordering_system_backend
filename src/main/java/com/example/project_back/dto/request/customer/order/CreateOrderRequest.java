package com.example.project_back.dto.request.customer.order;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateOrderRequest {
    private Long foodId;
    private Integer quantity;

    private Integer addressId;

    private Integer voucherId;

    private String paymentMethod;

}
