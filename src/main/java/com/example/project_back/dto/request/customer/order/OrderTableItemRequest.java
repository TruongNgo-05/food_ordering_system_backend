package com.example.project_back.dto.request.customer.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderTableItemRequest {
    private Long foodId;
    private Integer quantity;
}
