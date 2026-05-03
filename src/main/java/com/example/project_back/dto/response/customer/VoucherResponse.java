package com.example.project_back.dto.response.customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherResponse {
//    private String voucherCode;
    private String description;

    private Double discount; // số tiền giảm thực tế
    private Double minOrderValue;

    private Double totalBefore;     // tổng tiền giỏ hàng
    private Double totalAfter;      // tiền sau giảm

//    private Integer remaining;
}
