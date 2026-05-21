package com.example.project_back.dto.response.customer.order.reponseOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherOrder {
    private String voucherCode;
    private Double discount;
}
