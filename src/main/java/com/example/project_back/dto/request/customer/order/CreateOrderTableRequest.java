package com.example.project_back.dto.request.customer.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderTableRequest {
    private String tableNumber;

    private Integer paymentMethodId;

    private String customerName;

    private String customerPhone;

    private String note;

    private List<OrderTableItemRequest> items;

}
