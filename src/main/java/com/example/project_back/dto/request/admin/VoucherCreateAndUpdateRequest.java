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

    @DecimalMin(value = "0.0", message = "Minimum order value must >= 0")
    private Double minOrderValue;

    @Min(value = 0, message = "Usage limit must >= 0")
    private Integer usageLimit;

    @NotNull(message = "Start date must not be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date must not be null")
    private LocalDateTime endDate;
}
