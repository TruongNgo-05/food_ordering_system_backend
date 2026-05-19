package com.example.project_back.dto.request.customer.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderTableRequest {
    private Integer tableId;
    private Long foodId;
    private Integer quantity;
    private String customerName;
    private String customerPhone;
    private String note;
}
