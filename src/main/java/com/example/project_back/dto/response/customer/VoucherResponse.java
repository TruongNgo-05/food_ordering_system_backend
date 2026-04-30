package com.example.project_back.dto.response.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherResponse {
    private String voucherCode;
    private Double discount;
    private Double minOrderValue;
    private Integer remaining;
}
