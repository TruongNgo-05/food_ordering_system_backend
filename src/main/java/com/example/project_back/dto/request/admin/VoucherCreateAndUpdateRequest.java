package com.example.project_back.dto.request.admin;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class VoucherCreateAndUpdateRequest {

    @Size(max = 50, message = "Voucher code must be <= 50 characters")
    private String voucherCode;

    private String description;

    private Double discount;

    private Double minOrderValue;

    @Min(value = 0, message = "Usage limit must >= 0")
    private Integer usageLimit;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
