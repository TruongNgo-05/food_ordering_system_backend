package com.example.project_back.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonPropertyOrder({"id","voucherCode","description","discount","maxDiscount","minOrderValue","usageLimit","usedCount","startDate","endDate","createdAt"})
public class VoucherAdminDetailResponse {

    private Integer id;
    private String voucherCode;
    private String description;

    private Double discount;
    private Double minOrderValue;

    private Integer usageLimit;
    private Integer usedCount;
    private Integer remaining;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
}
