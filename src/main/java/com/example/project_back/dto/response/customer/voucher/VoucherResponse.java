package com.example.project_back.dto.response.customer.voucher;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherResponse {
    private String description;
    private Double discount;
    private Double totalPrice;
    private Double totalAfter;
}
